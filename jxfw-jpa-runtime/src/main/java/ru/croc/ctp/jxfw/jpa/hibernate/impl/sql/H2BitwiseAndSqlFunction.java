package ru.croc.ctp.jxfw.jpa.hibernate.impl.sql;

import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import java.util.List;

/**
 * SQL функция для операции Логическое И в H2SQL.
 *
 * @author Nosov Alexander
 *         on 22.05.15.
 */
public class H2BitwiseAndSqlFunction extends StandardSQLFunction implements SQLFunction {

    /**
     * @param name - имя функции.
     */
    public H2BitwiseAndSqlFunction(final String name) {
        super(name);
    }

    /**
     * @param name           - имя функции.
     * @param registeredType - возвращаемый тип.
     */
    public H2BitwiseAndSqlFunction(final String name, final Type registeredType) {
        super(name, registeredType);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("The function must be passed 2 arguments");
        }
        return "BITAND(" + arguments.get(0).toString() + ", " + arguments.get(1) + ")";
    }
}
