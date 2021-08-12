package ru.croc.ctp.jxfw.wc.web.config;

import org.apache.commons.io.FileUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Cодержит конфигурацию модуля по работе с бинарными данными,
 * которая может быть задана либо в рантайме, либо декларативно в main.json.config
 *
 * @author SMufazzalov
 * @since 05.07.2016
 */
public class XFilesModuleConfig extends HashMap<String, Object> {

    /**
     * Базовый Uri всех маршрутов: "api/_file/" //не поддерживается задание через конфиг.
     */
    public static final String API_ROUTE = "apiRoute";
    /**
     * Максимальный период хранения загруженных файлов.
     */
    public static final String FILES_MAX_AGE = "filesMaxAge";
    /**
     * Квота на размер загруженных файлов (в сумме) для одного пользователя.
     */
    public static final String QUOTA_PER_USER = "quotaPerUser";
    /**
     * Размер чанка (добавлено в версии WC Release 0.17)
     */
    public static final String UPLOAD_CHUNK_SIZE = "uploadChunkSize";

    /**
     * 1 GB.
     */
    public static final long DEFAULT_QUOTA_PER_USER = FileUtils.ONE_GB;

    /**
     * 2 дня.
     */
    public static final long DEFAULT_FILES_MAX_AGE = TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS);

    /**
     * @return Максимальный период хранения загруженных файлов.
     */
    public Long getFilesMaxAge() {
        Object age = get(FILES_MAX_AGE);
        if (age != null) {
            return new Long(age.toString());
        }
        return DEFAULT_FILES_MAX_AGE;
    }

    public String getApiRoute() {
        return get(API_ROUTE).toString();
    }

    /**
     * @return Размер чанка (добавлено в версии WC Release 0.17)
     */
    public Long getUploadChunkSize() {
        final Object chunk = get(UPLOAD_CHUNK_SIZE);
        if (chunk != null) {
            return new Long(chunk.toString());
        } else {
            return 0L; //TODO вычисление дефолтного значения в рантайм
            //UploadChunkSize явно не задано, то при инициализации сервера вычисляется наименьшее из значений
            // maxAllowedContentLength/maxRequestLength в конфигурации и устанавливается в качестве UploadChunkSize.
        }
    }

    /**
     * @return квота на размер загруженных файлов (в сумме) для одного пользователя.
     */
    public Long getQuotaPerUser() {
        Object quota = get(QUOTA_PER_USER);
        if (quota != null) {
            return new Long(quota.toString());
        }
        return DEFAULT_QUOTA_PER_USER;
    }
}
