package ru.croc.ctp.jxfw.jpa.exception.impl;

import static com.google.common.collect.Lists.newArrayList;

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import ru.croc.ctp.jxfw.jpa.exception.DbExceptionDescriptor;
import ru.croc.ctp.jxfw.jpa.exception.DbExceptionParser;
import ru.croc.ctp.jxfw.jpa.exception.XfwViolationType;

/**
 * Сервис разбора исключения, специфичного для СУБД PostgeSql типа.
 */
public class PostgreDbExceptionParser implements DbExceptionParser {


    @Override
    public DbExceptionDescriptor parse(Throwable throwable) {
        if (throwable instanceof PSQLException) {
            PSQLException psqlException = (PSQLException) throwable;
            ServerErrorMessage serverErrorMessage = psqlException.getServerErrorMessage();

            String table = serverErrorMessage.getTable();
            String column = serverErrorMessage.getColumn();
            String constraint = serverErrorMessage.getConstraint();
            String message = serverErrorMessage.getMessage();


            DbExceptionDescriptorImpl dbExceptionDescriptor = new DbExceptionDescriptorImpl();
            dbExceptionDescriptor.setCause(throwable);
            dbExceptionDescriptor.setDetails(serverErrorMessage.getDetail());
            dbExceptionDescriptor.setConstraint(constraint);
            dbExceptionDescriptor.setTableName(table);


            String sqlState = serverErrorMessage.getSQLState();
            switch (sqlState) {
                case "23505": // unique_violation
                    dbExceptionDescriptor.setXfwViolationType(XfwViolationType.Unique);
                    return dbExceptionDescriptor;

                case "23502": //not_null_violation
                    // эти констрейнты безымянные, поэтому определяем по имени столбца
                    dbExceptionDescriptor.setXfwViolationType(XfwViolationType.NotNull);
                    dbExceptionDescriptor.setColumnNames(newArrayList(column));
                    return dbExceptionDescriptor;

                case "23514":// check_violation
                    // TODO для check_violation Нет стандартного API jdbc для анализа ограничения
                    //поэтому можем передать только имя таблицы и  имя констрейнта, найти столбцы не можем.
                    dbExceptionDescriptor.setXfwViolationType(XfwViolationType.Check);
                    return dbExceptionDescriptor;

                case "23503"://foreign_key_violation
                    dbExceptionDescriptor.setXfwViolationType(XfwViolationType.ReferenceViolation);
                    return dbExceptionDescriptor;
                default:
                    return null;
            }

        }
        return null;
    }
}
