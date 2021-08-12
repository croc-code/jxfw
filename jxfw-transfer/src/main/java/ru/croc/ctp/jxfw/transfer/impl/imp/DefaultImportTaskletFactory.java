package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupProcessor;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReader;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupWriter;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportTaskletFactory;

/**
 * Фабрика для создания {@link ImportTasklet}, который реализует read => processor => writer операции импорта в рамках
 * одной транзакции.
 *
 * @since 1.6
 * @author Alexander Golovin
 */
@Component("importTaskletFactory")
public class DefaultImportTaskletFactory implements ImportTaskletFactory {
    @Override
    public ImportTasklet create(ImportGroupReader reader, ImportGroupProcessor processor, ImportGroupWriter writer) {
        return new ImportTasklet(reader, processor, writer);
    }
}
