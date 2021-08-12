package ru.croc.ctp.jxfw.transfer.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.google.common.io.Closer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.transfer.TransferService;
import ru.croc.ctp.jxfw.transfer.config.TransferConstants;
import ru.croc.ctp.jxfw.transfer.impl.exp.ExecuteExportRequest;
import ru.croc.ctp.jxfw.transfer.impl.imp.ExecuteImportRequest;
import ru.croc.ctp.jxfw.transfer.impl.imp.xml.ScenarioNameHandler;
import ru.croc.ctp.jxfw.transfer.impl.imp.xml.exception.BreakParsingException;
import ru.croc.ctp.jxfw.transfer.wc.OperationStateInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.croc.ctp.jxfw.transfer.wc.OperationStatus.byBatchStatus;

/**
 * @author Nosov Alexander
 * @since 1.4
 */
@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    private JobLauncher jobLauncher;

    private TaskExecutorWithInterrupt taskExecutorWithInterrupt;

    private JobExplorer jobExplorer;

    private JobOperator jobOperator;

    private ApplicationContext context;

    private ResourceStore localResourceStore;

    private TransferContextService transferContextService;

    private String instanceId;

    @Autowired
    public void setLocalResourceStore(ResourceStore resourceStore) {
        this.localResourceStore = resourceStore;
    }

    @Autowired
    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    @Autowired
    public void setJobOperator(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Autowired
    public void setTaskExecutorWithInterrupt(TaskExecutorWithInterrupt taskExecutorWithInterrupt) {
        this.taskExecutorWithInterrupt = taskExecutorWithInterrupt;
    }

    @Autowired
    public void setTransferContextService(TransferContextService transferContextService) {
        this.transferContextService = transferContextService;
    }

    /**
     * Идентификатор экземпляра приложения устанавливается в настройках(используется ид единный с ctp-scheduler).
     *
     * @param instanceId
     */
    @Autowired
    public void setInstanceId(@Value("${ru.croc.ctp.scheduler.instanceId}") String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    @Nonnull
    public String executeImport(@Nonnull ExecuteImportRequest request) {
        final String resourceId = request.getUploadedDataId();
        final String operationId = UUID.randomUUID().toString();

        log.info("Execute import operation (with uuid = {}) for file (id = {})", operationId,
            resourceId);

        String jobName = request.getScenarioName();
        if (jobName == null) {
            jobName = obtainScenarioNameFromFile(resourceId) + ".Import";
        }
        log.debug("Found scenario (job) name '{}'", jobName);


        final Job job = context.getBean(jobName, Job.class);

        final JobParametersBuilder builder = new JobParametersBuilder()
            .addDate(CREATION_DATE, new Date())
            .addString(RESOURCE_ID, resourceId)
            .addString(OPERATION_ID, operationId)
            .addString(INSTANCE_ID, instanceId)
            .addString(OPERATION_TYPE, TransferConstants.IMPORT_OPERATION_NAME);
        if (request.getAllParams() != null) {
            for (Map.Entry<String, String> param : request.getAllParams().entrySet()) {
                if (isKeyParameter(param.getKey())) {
                    continue;
                }
                builder.addString(param.getKey(), param.getValue());
            }
        }

        JobExecution jobExecution;
        try {
            synchronized (jobLauncher) {
                // не запускаем одновременно больше одного импорта для одного и тогоже ресурса.
                for (JobExecution currentjobExecution :
                    jobExplorer.findRunningJobExecutions(request.getScenarioName())) {
                    final String resourceIdCurrentJob = currentjobExecution.getJobParameters().getString(RESOURCE_ID);
                    if (resourceId != null && resourceId.equals(resourceIdCurrentJob)) {
                        log.error("The import with resourceId " + resourceId + " is running.");
                        throw new RuntimeException("Импорт для данного ресурса уже запущен.");
                    }
                }
                taskExecutorWithInterrupt.setCurrentOperationId(operationId);
                jobExecution = jobLauncher.run(job, builder.toJobParameters());
            }
        } catch (JobExecutionAlreadyRunningException
            | JobRestartException
            | JobInstanceAlreadyCompleteException
            | JobParametersInvalidException e) {
            log.error("Error during start operation import", e);
            throw new RuntimeException(e);
        }

        BatchStatus batchStatus = jobExecution.getStatus();
        log.debug("Batch status: {}", batchStatus.getBatchStatus());

        JobInstance jobInstance = jobExecution.getJobInstance();
        log.debug("Name of the job {}", jobInstance.getJobName());
        log.debug("Job instance Id: {}", jobInstance.getId());

        return operationId;
    }

    @Nonnull
    @Override
    public String executeExport(@Nonnull ExecuteExportRequest request) {
        final String operationId = UUID.randomUUID().toString();
        log.info("Execute export operation (with uuid = {})", operationId);

        final String jobName = request.getScenarioName();
        log.debug("Found scenario (job) name '{}'", jobName);


        final Job job = context.getBean(jobName, Job.class);

        final JobParametersBuilder builder = new JobParametersBuilder()
            .addDate("creationDate", new Date())
            .addString(SCENARIO_NAME, jobName)
            .addString(OPERATION_ID, operationId)
            .addString(INSTANCE_ID, instanceId)
            .addString(OPERATION_TYPE, TransferConstants.EXPORT_OPERATION_NAME);
        if (request.getAllParams() != null) {
            for (Map.Entry<String, String> param : request.getAllParams().entrySet()) {
                if (isKeyParameter(param.getKey())) {
                    continue;
                }
                builder.addString(param.getKey(), param.getValue());
            }
        }

        JobExecution jobExecution;
        try {
            taskExecutorWithInterrupt.setCurrentOperationId(operationId);
            jobExecution = jobLauncher.run(job, builder.toJobParameters());
        } catch (JobExecutionAlreadyRunningException
            | JobRestartException
            | JobInstanceAlreadyCompleteException
            | JobParametersInvalidException e) {
            log.error("Error during start operation export", e);
            throw new RuntimeException(e);
        }

        BatchStatus batchStatus = jobExecution.getStatus();
        log.debug("Batch status: {}", batchStatus.getBatchStatus());

        JobInstance jobInstance = jobExecution.getJobInstance();
        log.debug("Name of the job {}", jobInstance.getJobName());
        log.debug("Job instance Id: {}", jobInstance.getId());

        return operationId;
    }

    @Override
    @Nonnull
    public List<JobExecution> operations() {
        List<JobExecution> operations = new ArrayList<>();
        final List<String> jobNames = jobExplorer.getJobNames();
        for (final String jobName : jobNames) {
            final Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);
            operations.addAll(jobExecutions);
        }
        return operations;
    }

    @Nonnull
    @Override
    public List<JobExecution> operationsOfCurrentInstance() {
        return operations().stream()
            .filter(this::isCurrentInstanceOperation)
            .collect(Collectors.toList());
    }

    private boolean isCurrentInstanceOperation(JobExecution jobExecution) {
        return instanceId.equals(jobExecution.getJobParameters().getString(INSTANCE_ID));
    }

    @Override
    public OperationStateInfo getStatus(@Nonnull String opId) {
        final JobExecution operation = findByOperationId(opId);

        if (operation == null) {
            return null;
        }

        final OperationStateInfo stateInfo = new OperationStateInfo();
        stateInfo.setProgressPercent((byte) 100);
        stateInfo.setStatus(byBatchStatus(operation.getStatus()));
        stateInfo.setTimeStamp(LocalDateTime.now());
        stateInfo.setMessage(operation.getExitStatus().getExitDescription());

        if (operation.getStatus() == BatchStatus.FAILED) {
            final String failedMessage;
            if (operation.getExecutionContext().containsKey(FAILED_MESSAGE)) {
                failedMessage = operation.getExecutionContext().getString(FAILED_MESSAGE);
            } else {
                failedMessage = operation.getExitStatus().getExitDescription();
            }
            stateInfo.setErrorMessage(failedMessage);
            stateInfo.setMessage(failedMessage);
        }

        return stateInfo;
    }

    @Nullable
    @Override
    public String downloadResourceId(@Nonnull String operationId) {
        final JobExecution jobExecution = findByOperationId(operationId);
        if (jobExecution != null) {
            try {
                return transferContextService.loadLocalFileToResourceStore(jobExecution.getExecutionContext());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return null;
    }

    @Override
    public boolean abort(@Nonnull String operationId) {
        final JobExecution execution = findByOperationId(operationId);
        if (execution != null && execution.isRunning()) {
            try {
                jobOperator.stop(execution.getJobId());
                jobOperator.abandon(execution.getJobId());
                if (isCurrentInstanceOperation(execution)) {
                    taskExecutorWithInterrupt.stop(operationId);
                }
                return true;
            } catch (NoSuchJobExecutionException | JobExecutionAlreadyRunningException
                | JobExecutionNotRunningException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    private JobExecution findByOperationId(@Nonnull String opId) {
        final List<JobExecution> operations = allOperations();

        for (JobExecution operation : operations) {
            final JobParameters parameters = operation.getJobParameters();
            if (parameters.getString(OPERATION_ID).equals(opId)) {
                return operation;
            }
        }
        return null;
    }

    private List<JobExecution> allOperations() {
        List<JobExecution> operations = new ArrayList<>();
        final List<String> jobNames = jobExplorer.getJobNames();
        for (final String jobName : jobNames) {
            final List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 256);

            for (final JobInstance jobInstance : jobInstances) {
                final List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
                for (JobExecution jobExecution : jobExecutions) {
                    operations.add(jobExecution);
                }
            }
        }
        return operations;
    }


    /**
     * Получить имя сценаря из файла.
     *
     * @param resourceId ИД ресурса
     * @return имя сценария
     */
    @Nonnull
    protected String obtainScenarioNameFromFile(@Nonnull String resourceId) {
        log.debug("Find scenario name by resource id {}", resourceId);
        String scenarioName;

        try (InputStream stream = localResourceStore.getResourceStream(resourceId)) {

            if (isXmlLike(resourceId)) {
                scenarioName = obtainFromXml(stream);
            } else {
                scenarioName = obtainFromJson(stream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scenarioName;
    }

    /**
     * return true if the String passed in is something like XML.
     *
     * @param resourceId a string that might be XML
     * @return true of the string is XML, false otherwise
     */
    private boolean isXmlLike(String resourceId) {
        try (InputStream stream = localResourceStore.getResourceStream(resourceId)) {

            String firstLine;
            try (Closer closer = Closer.create()) {
                closer.register(stream);
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream));
                firstLine = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (firstLine != null && firstLine.trim().length() > 0) {
                return firstLine.trim().startsWith("<") && firstLine.contains("xml") && firstLine.contains(">");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private String obtainFromXml(InputStream stream) {
        String scenarioName;
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser parser;
        final ScenarioNameHandler handler = new ScenarioNameHandler();
        try {
            parser = factory.newSAXParser();
            parser.parse(stream, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        } catch (BreakParsingException e) {
            // ignore
        }
        scenarioName = handler.getScenarioName();

        return scenarioName;
    }

    private String obtainFromJson(InputStream stream) {
        String scenarioName = "";
        try {
            JsonFactory factory = new MappingJsonFactory();
            JsonParser jp =
                factory.createParser(stream);

            JsonToken current;

            current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                log.error("Error: root should be object: quiting.");
                return "";
            }

            boolean foundScenarioName = false;
            while (!foundScenarioName) {
                jp.nextToken();

                String fieldName = jp.getCurrentName();
                // move from field name to field value
                if (fieldName.equals("scenarioName")) {
                    JsonNode node = jp.readValueAsTree();

                    scenarioName = node.get("scenarioName").textValue();
                    foundScenarioName = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scenarioName;
    }


    /**
     * Проверяет является ли параметр ключевым.
     *
     * @param parameterName название параметра
     * @return true если является, иначе false.
     */
    private boolean isKeyParameter(String parameterName) {
        if (parameterName == null) {
            return false;
        }

        switch (parameterName) {
            case SCENARIO_NAME:
            case OPERATION_ID:
            case RESOURCE_ID:
            case CREATION_DATE:
            case OPERATION_TYPE:
                return true;

            default:
                return false;
        }
    }
}
