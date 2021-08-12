package ru.croc.ctp.jxfw.solr.config;

import com.google.common.io.ByteStreams;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

/**
 * @author SMufazzalov конвертеры типов, на запись и чтение из доменный модели в
 *         solr которые из коробки solr не понимает.
 */
public class CustomSolrConverters extends ArrayList<Converter> {

    private static CustomSolrConverters INSTANCE;

    /**
     * Регистрация конвертеров типов доменныой модели, которые поддерживаются jXFW.
     * @return CustomSolrConverters
     */
    public static CustomSolrConverters getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomSolrConverters();
        }
        return INSTANCE;
    }

    private CustomSolrConverters() {
        add(LocalDateTimeToDateConverter.INSTANCE);
        add(DateToLocalDateTimeConverter.INSTANCE);
        add(BlobToBase64StringConverter.INSTANCE);
        add(ByteArrayToBlobConverter.INSTANCE);
        add(Base64StringToBlobConverter.INSTANCE);
        add(ZonedDateTimeToStringConverter.INSTANCE);
        add(DateToZonedDateTimeConverter.INSTANCE);
    }

    /**
     * LocalDateTime, Date конвертер.
     */
    @WritingConverter
    public enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public Date convert(LocalDateTime source) {
            if (source == null) {
                return null;
            }

            return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * Date, LocalDateTime конвертер.
     */
    @ReadingConverter
    public enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public LocalDateTime convert(Date source) {
            if (source == null) {
                return null;
            }
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(source);
            ZonedDateTime zdt = cal.toZonedDateTime();
            return zdt.toLocalDateTime();
        }
    }

    /**
     * LocalDateTime, String конвертер.
     */
    public enum LocalDateTimeToUtcStringConverter implements Converter<LocalDateTime, String> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public String convert(LocalDateTime source) {
            if (source == null) {
                return "";
            }
            // toString() returns ISO 8601 format, e.g. 2014-02-15T01:02:03Z
            String dateStr = source.atZone(ZoneId.systemDefault()).toInstant().toString();

            return ClientUtils.escapeQueryChars(dateStr);
        }
    }

    /**
     * ZonedDateTime, String конвертер.
     */
    public enum ZonedDateTimeToUtcStringConverter implements Converter<ZonedDateTime, String> {
        /**
         * {@link Converter}.
         */
        INSTANCE;
        
        @Override
        public String convert(ZonedDateTime source) {
            if (source == null) {
                return "";
            }
            String dateStr = source.format(DateTimeFormatter.ISO_INSTANT);
            
            return ClientUtils.escapeQueryChars(dateStr);
        }
    }

    /**
     * Blob, String конвертер.
     */
    @WritingConverter
    public enum BlobToBase64StringConverter implements Converter<Blob, String> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public String convert(Blob source) {
            if (source == null) {
                return null;
            }
            
            String res = null;
            try {
                res = Base64.getEncoder().encodeToString(ByteStreams.toByteArray(source.getBinaryStream()));
            } catch (IOException e) {
                throw new RuntimeException(e.getCause());
            } catch (SQLException e) {
                throw new RuntimeException(e.getCause());
            }
            
            return res;
        }
    }

    /**
     * String, Blob конвертер.
     */
    @ReadingConverter
    public enum Base64StringToBlobConverter implements Converter<String, Blob> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public Blob convert(String source) {
            if (source == null) {
                return null;
            }

            try {
                byte[] decode = Base64.getDecoder().decode(source);
                return new SerialBlob(decode);
            } catch (SerialException e) {
                throw new RuntimeException(e.getCause());
            } catch (SQLException e) {
                throw new RuntimeException(e.getCause());
            }

        }
    }

    /**
     * byte[], Blob конвертер.
     */
    @ReadingConverter
    public enum ByteArrayToBlobConverter implements Converter<byte[], Blob> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public Blob convert(byte[] source) {
            if (source == null) {
                return null;
            }

            try {
                return new SerialBlob(source);
            } catch (SerialException e) {
                throw new RuntimeException(e.getCause());
            } catch (SQLException e) {
                throw new RuntimeException(e.getCause());
            }

        }
    }

    /**
     * ZonedDateTime, String конвертер.
     *
     */
    @WritingConverter
    public enum ZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public String convert(ZonedDateTime source) {
            if (source == null) {
                return "";
            }
            
            String dateStr = source.format(DateTimeFormatter.ISO_INSTANT);
            return dateStr;
        }
    }

    /**
     * Date, ZonedDateTime конвертер.
     */
    @ReadingConverter
    public enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        /**
         * {@link Converter}.
         */
        INSTANCE;

        @Override
        public ZonedDateTime convert(Date source) {
            if (source == null) {
                return null;
            }

            return ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }
}
