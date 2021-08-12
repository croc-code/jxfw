package ru.croc.ctp.jxfw.transfer.impl.exp;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Запрос на выполнение экспорта.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class ExecuteExportRequest {


    private final String scenarioName;
    private final Map<String, String> allParams;

    private ExecuteExportRequest(@Nonnull ExecuteExportRequestBuilder builder) {
        this.scenarioName = builder.scenarioName;
        this.allParams = builder.allParams;
    }

    /**
     * @return наименовние сценария (имя {@link org.springframework.batch.core.Job}).
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * @return дополнительные параметры экспорта.
     */
    public Map<String, String> getAllParams() {
        return allParams;
    }

    /**
     * Билдер для создания объекта {@link ExecuteExportRequest}.
     */
    public static class ExecuteExportRequestBuilder {

        private String scenarioName;
        private Map<String, String> allParams;

        /**
         * Установить имя сценария операции.
         *
         * @param name имя сценария операции
         * @return билдр.
         */
        public ExecuteExportRequestBuilder scenarioName(@Nullable String name) {
            this.scenarioName = name;
            return this;
        }

        /**
         * Установить все параметры.
         *
         * @param allParams параметры.
         * @return билдер.
         */
        public ExecuteExportRequestBuilder allParams(Map<String, String> allParams) {
            this.allParams = allParams;
            return this;
        }

        /**
         * Построить объект типа {@link ExecuteExportRequest} на основе данных билдера.
         *
         * @return объект типа {@link ExecuteExportRequest}
         */
        public ExecuteExportRequest build() {
            return new ExecuteExportRequest(this);
        }
    }
}
