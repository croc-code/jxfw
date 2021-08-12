package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupProcessor;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReader;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupWriter;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;

import java.util.Arrays;

/**
 * {@link Tasklet}, который реализует read => processor => writer операции импорта в рамках одной транзакции.
 *
 * @since 1.6
 * @author Alexander Golovin
 */
public class ImportTasklet implements Tasklet {
    private ImportGroupReader reader;
    private ImportGroupProcessor processor;
    private ImportGroupWriter writer;


    /**
     * {@link Tasklet}, который реализует read => processor => writer операции импорта в рамках одной транзакции.
     *
     * @param reader reader
     * @param processor processor
     * @param writer writer
     */
    public ImportTasklet(ImportGroupReader reader, ImportGroupProcessor processor, ImportGroupWriter writer) {
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        reader.beforeStep(chunkContext.getStepContext().getStepExecution());

        ImportGroup group = reader.read();
        while (group != null) {
            group = processor.process(group);
            writer.write(Arrays.asList(group));

            group = reader.read();
        }

        reader.afterStep(chunkContext.getStepContext().getStepExecution());
        return RepeatStatus.FINISHED;
    }
}
