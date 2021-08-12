package ru.croc.ctp.jxfw.core.load.context;

/**
 * Ключи дополнительных атрибутов контекста загрузки.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public interface LoadContextKeys {

    /**
     * Загрузка содержимого поля типа Blob.
     */
    String BLOB_CONTENT = "load_context.blobContent";
    
    /**
     * Контекст сохранения, в случае если контекст загрузки должен быть с ним связан.
     */
    String STORE_CONTEXT = "load_context.storeContext";
}
