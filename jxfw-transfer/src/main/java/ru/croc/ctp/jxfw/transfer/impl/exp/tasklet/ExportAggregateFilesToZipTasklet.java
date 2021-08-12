package ru.croc.ctp.jxfw.transfer.impl.exp.tasklet;

import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService.LocalFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * {@link Tasklet} агрегирующий файлы указанные в контексте в zip-архив
 * и устанвливающий архив как итоговый файл.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class ExportAggregateFilesToZipTasklet implements Tasklet {
    private final TransferContextService transferContextService;
    private final String fileName;

    /**
     * {@link Tasklet} агрегирующий файлы указанные в контексте в zip-архив
     * и устанвливающий архив как итоговый файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param fileName имя файла архива(без разрешения).
     */
    public ExportAggregateFilesToZipTasklet(TransferContextService transferContextService, String fileName) {
        this.transferContextService = transferContextService;
        this.fileName = fileName;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalFile zipLocalFile = transferContextService.generateLocalFile(fileName + ".zip");

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipLocalFile.path))) {
            for (LocalFile localFile : transferContextService.getLocalFiles(chunkContext)) {
                zip.putNextEntry(new ZipEntry(localFile.fileName));
                try (FileInputStream in = new FileInputStream(localFile.path)) {
                    Streams.copy(in, zip, false);
                }
            }
        }

        transferContextService.setLocalFiles(chunkContext, Collections.singletonList(zipLocalFile));
        return RepeatStatus.FINISHED;
    }


}
