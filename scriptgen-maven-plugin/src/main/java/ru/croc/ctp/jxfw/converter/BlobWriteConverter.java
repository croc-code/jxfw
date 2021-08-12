package ru.croc.ctp.jxfw.converter;

import org.springframework.core.convert.converter.Converter;

import java.nio.ByteBuffer;
import java.sql.Blob;

/**
 * Created by SMufazzalov on 15.06.2017.
 */
public class BlobWriteConverter implements Converter<Blob, ByteBuffer> {
    @Override
    public ByteBuffer convert(Blob source) {
        return null; //для генерации скрипта этого достаточно!!!
    }
}
