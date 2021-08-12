package ru.croc.ctp.jxfw.transfer.impl.exp;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportDomainToWriter;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportItemFileWriterFactory;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService.LocalFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Writer {@link DomainTo} объектов в динамически формируемое множество файлов.
 * Файл в который попадёт объект определяется переданной функцией.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class ExportDynamicFileItemWriter implements ExportDomainToWriter {
    private final Map<String, AbstractFileItemWriter> writers;
    private final ExportItemFileWriterFactory exportItemFileWriterFactory;
    private final Function<DomainTo, String> fileNameFunction;
    private final TransferContextService transferContextService;

    /**
     * Writer {@link DomainTo} объектов в несколько файлов.
     *
     * @param fileNameFunction функция распределения объектов по файлам
     * @param exportItemFileWriterFactory фабрика writer'ов
     * @param transferContextService сервис для работы с контекстом.
     */
    public ExportDynamicFileItemWriter(Function<DomainTo, String> fileNameFunction,
                                       ExportItemFileWriterFactory exportItemFileWriterFactory,
                                       TransferContextService transferContextService) {
        this.fileNameFunction = fileNameFunction;
        this.exportItemFileWriterFactory = exportItemFileWriterFactory;
        this.transferContextService = transferContextService;
        writers = new HashMap<>();
    }

    @Override
    public void write(List<? extends DomainTo> items) throws Exception {
        final Map<String, List<DomainTo>> groups = items.stream()
                .collect(Collectors.groupingBy(fileNameFunction));
        initNewWriters(new ArrayList<>(groups.keySet()));

        for (String file : groups.keySet()) {
            try (AbstractFileItemWriter writer = writers.get(file)) {
                writer.write(groups.get(file));
            }
        }
    }

    /**
     * Создаёт недостающие writer'ы.
     *
     * @param files список имен файлов в которые должна произойти запись.
     * @throws IOException проблема при работе с фалом.
     */
    private void initNewWriters(List<String> files) throws IOException {
        for (String file : files) {
            if (writers.containsKey(file)) {
                continue;
            }

            // создаём файл с пустой коллекцией объектов
            try (AbstractFileItemWriter writer = (AbstractFileItemWriter) exportItemFileWriterFactory.create(file)) {
                writers.put(file, writer);
            }
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // nothing
    }

    /**
     * Сохраняем список идентификаторов файлов в контекст.
     *
     * @param stepExecution выполняемый шаг.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution)  {
        final List<LocalFile> localFiles = writers.values().stream()
                .map(AbstractFileItemWriter::getLocalFile)
                .collect(Collectors.toList());
        try {
            transferContextService.addLocalFiles(stepExecution, localFiles);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return stepExecution.getExitStatus();
    }
}
