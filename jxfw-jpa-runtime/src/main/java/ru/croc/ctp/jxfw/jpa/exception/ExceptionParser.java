package ru.croc.ctp.jxfw.jpa.exception;

import static com.google.common.collect.Lists.newArrayList;

import org.hibernate.TransientPropertyValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XIntegrityViolationException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.jpa.exception.impl.ExceptionDescriptorImpl;
import ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwMetadataHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Сервис занимается разбором исключений уровня БД. Развертывает исключение,
 * определяет какая БД. В зависимости от этого разбирает исключение, формирует
 * ExceptionDescriptor?, получает метаданные из XfwMetadataHolder и передает формирование исключения
 * в dispatchingExceptionBuilder.
 *
 * @author OKrutova
 * @since 1.6
 */
@Service
public class ExceptionParser {

    /**
     * Логгер.
     */
    static Logger logger = LoggerFactory.getLogger(ExceptionParser.class);


    private final ExceptionBuilder dispatchingExceptionBuilder;

    private final List<DbExceptionParser> dbExceptionParsers;

    private final XfwMetadataHolder xfwMetadataHolder;


    /**
     * Конструктор.
     *
     * @param xfwMetadataHolder           метаданные, собранные с Hibernate о связях имен таблиц и констрейнтов с
     *                                    метамоделью.
     * @param dispatchingExceptionBuilder диспетчер обработчиков исключений.
     * @param dbExceptionParsers          список парсеров исключений для разных БД
     */
    @Autowired
    public ExceptionParser(XfwMetadataHolder xfwMetadataHolder,
                           ExceptionBuilder dispatchingExceptionBuilder,
                           @Autowired(required = false) List<DbExceptionParser> dbExceptionParsers) {
        this.dispatchingExceptionBuilder = dispatchingExceptionBuilder;
        this.dbExceptionParsers = dbExceptionParsers == null ? newArrayList() : dbExceptionParsers;
        this.xfwMetadataHolder = xfwMetadataHolder;
    }



    /**
     * Конструктор.
     *
     * @param xfwMetadataHolder           метаданные, собранные с Hibernate о связях имен таблиц и констрейнтов с
     *                                    метамоделью.
     * @param dispatchingExceptionBuilder диспетчер обработчиков исключений.
     * @deprecated
     */
    @Deprecated
    public ExceptionParser(XfwMetadataHolder xfwMetadataHolder, ExceptionBuilder dispatchingExceptionBuilder) {
        this.xfwMetadataHolder = xfwMetadataHolder;
        this.dispatchingExceptionBuilder = dispatchingExceptionBuilder;
        this.dbExceptionParsers = newArrayList();
    }

    /**
     * Пытается разобрать исключение от слоя хранения.
     *
     * @param ex           исходное исключение
     * @param storeContext контекст сохранения. До решения JXFW-1415 в нем тольок локаль.
     * @return исключение jXFW, если смогли разобрать, иначе null
     */

    public XException parseException(DataAccessException ex, StoreContext storeContext) {

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (ex instanceof DataIntegrityViolationException) {
            return parseDataIntegrityViolationException(mostSpecificCause, storeContext);
        } else if (ex instanceof InvalidDataAccessApiUsageException) {
            //Этот код не вызывается.
            return parseInvalidDataAccessApiUsageException(mostSpecificCause);
        } else {
            return null;
        }
    }


    /**
     * Пытается разобрать исключение в зависимости от используемой БД.
     * Поддерживает: PostgreSql, H2
     * TBD: MsSql, Oracle
     *
     * @param throwable    исходное исключение
     * @param storeContext контекст сохранения. До решения JXFW-1415 в нем тольок локаль.
     * @return исключение jXFW, если смогли разобрать, иначе null
     */
    private XIntegrityViolationException parseDataIntegrityViolationException(Throwable throwable,
                                                                              StoreContext storeContext) {

        Optional<DbExceptionDescriptor> result = dbExceptionParsers.stream()
                .map(dbExceptionParser -> dbExceptionParser.parse(throwable))
                .filter(Objects::nonNull)
                .findFirst();

        if (result.isPresent()) {
            ExceptionDescriptor exceptionDescriptor = new ExceptionDescriptorImpl(xfwMetadataHolder, result.get(),
                    storeContext);
            switch (exceptionDescriptor.getViolationType()) {
                case Unique:
                    return dispatchingExceptionBuilder.buildUniqueException(exceptionDescriptor);
                case Check:
                    return dispatchingExceptionBuilder.buildCheckViolationException(exceptionDescriptor);
                case NotNull:
                    return dispatchingExceptionBuilder.buildNotNullException(exceptionDescriptor);
                case ReferenceViolation:
                    return dispatchingExceptionBuilder.buildFkException(exceptionDescriptor);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }


    /**
     * Пытается разобрать исключение от ORM (Hibernate).
     * Этот код не вызывается. Исключения ORM перехватываются
     * в DataAccessExceptionHandler и обрабатываются единообразно.
     *
     * @param throwable исходное исключение
     * @return исключение jXFW, если смогли разобрать, иначе null
     */
    private XException parseInvalidDataAccessApiUsageException(Throwable throwable) {
        if (throwable instanceof TransientPropertyValueException) {
            TransientPropertyValueException valueException = (TransientPropertyValueException) throwable;
            XIntegrityViolationException.Builder builder = new XIntegrityViolationException.Builder<>(
                    "ru.croc.ctp.jxfw.jpa.hibernate.metadata.transient.value",
                    "Not-null property {0} of object {1} references a transient value {2} "
                            + "- transient instance must be saved before current operation")
                    .addArgument(XfwMessageTemplate.formatPropertyNamePlaceholder(
                            valueException.getPropertyOwnerEntityName(),
                            valueException.getPropertyName()
                    ))
                    .addArgument(XfwMessageTemplate.formatTypeNamePlaceholder(
                            valueException.getPropertyOwnerEntityName()
                    ))
                    .addArgument(XfwMessageTemplate.formatTypeNamePlaceholder(
                            valueException.getTransientEntityName()
                    ));

            return builder.build();


        }
        return null;

    }

}
