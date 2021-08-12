package ru.croc.ctp.jxfw.core.facade.webclient.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import javax.validation.constraints.NotNull;

/**
 * Реализация {@link java.sql.Blob} для получения содержимого файла,
 * хранимого в {@link ResourceStore}.
 * <p/>
 * Created by SPlaunov on 27.03.2016.
 */
public class ResourceStoreBlob implements Blob, Serializable {

    private static final long serialVersionUID = 1L;

    private final ResourceStore resourceStore;

    private final String resourceId;

    private InputStream stream = null;

    private ResourceProperties resourceProperties = null;

    private Long length = null;

    /**
     * Конструктор.
     *
     * @param resourceStore Экземпляр временного хранилища загруженных файлов
     * @param resourceId    Идентификатор загруженного ресурса (файла)
     */
    public ResourceStoreBlob(ResourceStore resourceStore, String resourceId) {

        this.resourceStore = resourceStore;
        this.resourceId = resourceId;
    }

    @Override
    public long length() throws SQLException {
        if (length == null) {
            ResourceProperties resourceProperties = resourceStore.getResourceProperties(resourceId);
            if (resourceProperties != null) {
                length = resourceProperties.getContentSize();
            } else {
                throw new IllegalStateException("Resource not found in resource store: " + resourceId);
            }
        }
        return length;
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        if (stream == null || !isStreamAvailable(stream)) {
            stream = resourceStore.getResourceStream(resourceId);
        }
        return stream;
    }

    @Override
    public InputStream getBinaryStream(long pos, long length)
            throws SQLException {
        throw new UnsupportedOperationException();
    }

    private boolean isStreamAvailable(InputStream stream) {
        try {
            return stream.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len)
            throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void truncate(long len) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void free() throws SQLException {
        throw new UnsupportedOperationException();
    }


    protected ResourceStore getResourceStore() {
        return resourceStore;
    }

    protected String getResourceId() {
        return resourceId;
    }

    /**
     * Возвращает свойства ресурса.
     *
     * @return Свойства ресурса
     */
    @NotNull
    ResourceProperties getResourceProperties() {
        if (resourceProperties == null) {
            resourceProperties = resourceStore.getResourceProperties(resourceId);
            if (resourceProperties == null) {
                throw new IllegalStateException("Resource not found in the store: " + resourceId);
            }
        }
        return resourceProperties;
    }
    
    /**
     * Реализация, позволяющая копировать состояние доменных объектов, 
     * @return {@code null}
     */
    private Object writeReplace() {
        return null;
    }
}
