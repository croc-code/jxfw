package ru.croc.ctp.jxfw.core.facade.webclient.file;

/**
 * Контейнер свойств ресурса.
 */
public class ResourceProperties {
    private String contentType;
    private String fileName;
    private Long contentSize;

    /**
     * Конструктор.
     *
     * @param contentType Тип контента (MIME)
     * @param fileName    Имя файла
     * @param contentSize Размер контента в байтах
     */
    public ResourceProperties(String contentType, String fileName, Long contentSize) {
        this.contentType = contentType;
        this.fileName = fileName;
        this.contentSize = contentSize;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getContentSize() {
        return contentSize;
    }
}
