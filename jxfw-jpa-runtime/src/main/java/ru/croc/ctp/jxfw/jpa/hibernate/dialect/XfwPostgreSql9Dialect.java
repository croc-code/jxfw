package ru.croc.ctp.jxfw.jpa.hibernate.dialect;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.hibernate.dialect.LobMergeStrategy;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.type.StandardBasicTypes;

import ru.croc.ctp.jxfw.jpa.hibernate.impl.sql.PostgreSqlBitwiseAndSqlFunction;

/**
 * Расширение диалекта для PostgreSQL
 *
 * @author Nosov Alexander
 *         on 22.05.15.
 */
public class XfwPostgreSql9Dialect extends PostgreSQL9Dialect {

    /**
     * Название функции логического И.
     */
    public static final String BITWISE_AND_FUNCTION_NAME = "bitwise_and";

    /**
     * Конструктор, в которой регистрируются доп. функции.
     */
    public XfwPostgreSql9Dialect() {
        super();
        registerFunction(BITWISE_AND_FUNCTION_NAME,
                new PostgreSqlBitwiseAndSqlFunction(BITWISE_AND_FUNCTION_NAME, StandardBasicTypes.INTEGER));
    }
    
    @Override
    public LobMergeStrategy getLobMergeStrategy() {
        return XFW_NEW_LOCATOR_LOB_MERGE_STRATEGY;
    }
    
    /**
     * FIXME: избавиться от дублирования, пока оставлено для возможности компиляции в JDK 7.
     * Merge strategy based on creating a new LOB locator.
     */
    protected static final LobMergeStrategy XFW_NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
        
        @Override
        public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
            if ( original == null && target == null ) {
                return null;
            }
            JdbcServices jdbcServices = session.getFactory().getJdbcServices();
            try {
                final LobCreator lobCreator = jdbcServices.getLobCreator(session);
                return original == null
                        ? lobCreator.wrap(new SerialBlob(ArrayHelper.EMPTY_BYTE_ARRAY))
                        : original instanceof Serializable 
                            ? lobCreator.wrap(original) 
                            : lobCreator.createBlob(original.getBinaryStream(), original.length());
            } catch (SQLException e) {
                throw jdbcServices.getSqlExceptionHelper().convert( e, "unable to merge BLOB data" );
            }
        }

        @Override
        public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
            return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob(original, target, session);
        }

        @Override
        public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
            return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob(original, target, session);
        }
    };
}