package ru.croc.ctp.jxfw.generator;

import org.apache.maven.plugin.logging.Log;
import ru.croc.ctp.jxfw.facade.transfer.ToServiceCreator;
import ru.croc.ctp.jxfw.facade.transfer.TransferToServiceCreator;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Генератор фасада модуля Transfer по Ecore модели.
 *
 * @author Alexander Golovin
 * @since  1.6
 */
public class TransferFacadeGenerator extends AbstractFacadeGenerator {
    /**
     * Генератор фасада модуля Transfer по Ecore модели.
     *
     * @param sourceFolder Папка, в которой будут найдены файлы моделей.
     * @param outputFolder Папка, в которую будет сгенерирован код.
     * @param options      Набор опций для задачи.
     * @param log          Логгер maven.
     */
    public TransferFacadeGenerator(Path sourceFolder, Path outputFolder, Map<String, Object> options, Log log) {
        super(sourceFolder, outputFolder, options, log);
    }

    @Override
    protected void generate(Set<XFWClass> classes) {
        log.debug("Start generating the transfer facade.");

        final ToServiceCreator serviceToCreator = new TransferToServiceCreator();
        classes.stream()
                .filter(cls -> cls.getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) == null)
                .forEach(xfwClass -> {
                    if (xfwClass.isTransientType() || xfwClass.isPersistentType()) {
                        log.debug("Start creating TO service for class " + xfwClass.getInstanceClassName());
                        serviceToCreator.create(xfwClass, options).forEach(this::save);
                        log.debug("Finish creating TO service for class " + xfwClass.getInstanceClassName());
                    }
                });

        log.debug("Finish generating the transfer facade.");
    }
}
