package ru.croc.ctp.jxfw.reporting.xslfo.exception;

/**
 * Ошибка указывающая что применяется неподдерживаемый тип агрегирующей функции.
 * известные тут {@link ru.croc.ctp.jxfw.reporting.xslfo.types.AggregationFunctionTypeClass}
 */
public class AggregateFunctionUnknownException extends RuntimeException {
    /**
     * Конструктор.
     * @param functionName имя агрегирующей функции
     */
    public AggregateFunctionUnknownException(String functionName) {
        super("Aggregate function is not supported: " + functionName);
    }
}
