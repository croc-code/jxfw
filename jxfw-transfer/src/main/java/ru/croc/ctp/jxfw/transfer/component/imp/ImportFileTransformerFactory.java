package ru.croc.ctp.jxfw.transfer.component.imp;

/**
 * Фабрика преобразователей файлов пред импортом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public interface ImportFileTransformerFactory {
    /** Создаёт преобразователь файлов перед импортом, по указаному типу и логики преобразования.
     * @param handler преобразователь DTO объектов.
     * @return преобразователь файлов.
     */
    ImportFileTransformer create(ImportTransformHandler handler);

}
