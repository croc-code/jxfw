package ru.croc.ctp.jxfw.core.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Сериализуемая обертка для несериализуемого блоба. До сериализации проксирует все методы к исходному блобу.
 * Внимание: после сериализации/десериализации блоб нельзя использовать т.к. ссылка на исходный блоб исчезла.
 *
 * @author Alexander Golovin
 * @since 1.7.2
 */
public final class BlobSerialWrapper implements Blob, Serializable {
    private final transient Blob blob;

    /**
     * Сериализуемая обертка для несериализуемого блоба
     *
     * @param blob исходный блоб.
     */
    public BlobSerialWrapper(Blob blob) {
        this.blob = blob;
    }

    private void checkInit() {
        if (blob == null) {
            throw new IllegalStateException("The blob is not initialized!");
        }
    }

    @Override
    public long length() throws SQLException {
        checkInit();
        return blob.length();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        checkInit();
        return blob.getBytes(pos, length);
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        checkInit();
        return blob.getBinaryStream();
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        checkInit();
        return blob.position(pattern, start);
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        checkInit();
        return blob.position(pattern, start);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        checkInit();
        return blob.setBytes(pos, bytes);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        checkInit();
        return blob.setBytes(pos, bytes, offset, len);
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        checkInit();
        return blob.setBinaryStream(pos);
    }

    @Override
    public void truncate(long len) throws SQLException {
        checkInit();
        blob.truncate(len);
    }

    @Override
    public void free() throws SQLException {
        checkInit();
        blob.free();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        checkInit();
        return blob.getBinaryStream(pos, length);
    }
}
