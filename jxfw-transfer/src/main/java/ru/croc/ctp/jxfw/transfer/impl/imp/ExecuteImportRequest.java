package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.springframework.util.Assert;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Запрос на выполнение импорта.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class ExecuteImportRequest {

    private final String uploadedDataId;

    private final String scenarioName;
    private final Map<String, String> allParams;

    private ExecuteImportRequest(@Nonnull ExecuteImportRequestBuilder builder) {
        Assert.notNull(builder.uploadedDataId, "Upload Id must not be Null");

        this.uploadedDataId = builder.uploadedDataId;
        this.scenarioName = builder.scenarioName;
        this.allParams = builder.allParams;
    }

    public String getUploadedDataId() {
        return uploadedDataId;
    }

    /**
     * @return имя {@link org.springframework.batch.core.Job}.
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * @return дополнительные параметры импорта.
     */
    public Map<String, String> getAllParams() {
        return allParams;
    }

    /**
     * Билдер для создания объекта {@link ExecuteImportRequest}.
     */
    public static class ExecuteImportRequestBuilder {
        private String uploadedDataId;

        private String scenarioName;
        private Map<String, String> allParams;

        /**
         * Установить идентификатор для загруженного файла с данными.
         *
         * @param id идентификатор для загруженного файла с данными.
         * @return билдр
         */
        public ExecuteImportRequestBuilder uploadedDataId(@Nonnull String id) {
            this.uploadedDataId = id;
            return this;
        }

        /**
         * Установить имя сценария операции.
         *
         * @param name имя сценария операции
         * @return билдр
         */
        public ExecuteImportRequestBuilder scenarioName(@Nullable  String name) {
            this.scenarioName = name;
            return this;
        }

        /**
         * Установить все параметры.
         *
         * @param allParams параметры.
         * @return билдер.
         */
        public ExecuteImportRequestBuilder allParams(Map<String, String> allParams) {
            this.allParams = allParams;
            return this;
        }

        /**
         * Построить объект типа {@link ExecuteImportRequest} на основе данных билдера.
         *
         * @return объект типа {@link ExecuteImportRequest}
         */
        public ExecuteImportRequest build() {
            return new ExecuteImportRequest(this);
        }
    }
}
