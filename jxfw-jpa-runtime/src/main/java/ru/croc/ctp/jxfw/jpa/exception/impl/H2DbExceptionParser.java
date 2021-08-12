package ru.croc.ctp.jxfw.jpa.exception.impl;

import static org.apache.commons.lang3.StringUtils.split;

import org.apache.commons.lang3.StringUtils;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.jpa.exception.DbExceptionDescriptor;
import ru.croc.ctp.jxfw.jpa.exception.DbExceptionParser;
import ru.croc.ctp.jxfw.jpa.exception.XfwViolationType;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Сервис разбора исключения, специфичного для СУБД H2 типа.
 * Поддерживает обработку следующих ограничений:
 * <ol>
 * <li>ограничение по уникальности. Здесь имеется особенность поведения БД H2. Несмотря на то, что в модели явно указано
 * имя индекса @Index(name="unique_name_age",...), Hibernate при создании схемы формирует запрос вида:
 * alter table client add <b>constraint</b> unique_name_age unique (name, age)
 * В результате чего H2 создает констрейнт с заданным именем и соответствующий индекс с автогенерированным именем вида
 * UNIQUE_NAME_AGE_INDEX_7. Данный класс работает с учетом этого поведения и отрезает от имени индекса
 * автосгенеренные суффиксы.
 * Но если индекс был создан без помощи Hibernate, явным вызовом SQL, то имя индекса не будет соответствовать шаблону
 * для разбора
 * и исключение не обработается.
 * https://hibernate.atlassian.net/projects/HHH/issues/HHH-13148
 * </li>
 * <li>ограничение NOT NULL</li>
 * <li>проверки ссылочной целостности</li>
 * </ol>
 * <ol>
 * Поддержаны частично:
 * <li>ограничение CHECK (не проработано, как задать имя ограничения в аннотациях JPA/Hibernate, и, следовательно, не
 * понятно,
 * как в кастомных билдерах исключений понимать, что за констрейнт сработал; имя ограничения автосгенеренное) .
 * Т.к. нет реальных примеров того, как задают ограничения в проектах, реализацию откладываем</li>
 * </ol>
 */
public class H2DbExceptionParser implements DbExceptionParser {


    /**
     * Шаблон выделения информации из сообщения об ошибке при срабатывании ограничения БД об уникальности.
     * <p/>
     * Выделяются следующие группы:
     * <ol>
     * <li>Идентификатор ограничения.</li>
     * <li>Наименование таблицы.</li>
     * <li>Список полей, входящих в ограничение, разделённый запятыми.</li>
     * </ol>
     */
    private static final Pattern PATTERN_DUPLICATE_KEY_INFO = Pattern.compile(
            "(?:.*):\\s\"([^\\s]*)_(?:[^\\s_]*)_(?:[^\\s_]*)\\sON\\sPUBLIC\\.([^\\(\\)]*)\\(([^\\(\\)]*)\\)\\sVALUES",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNICODE_CASE);

    /**
     * Шаблон выделения информации из сообщения об ошибке при срабатывании ограничения на обязательность заполнения
     * поля.
     * <p/>
     * Выделяются следующие группы:
     * <ol>
     * <li>Идентификатор поля.</li>
     * <li>Наименование таблицы.</li>
     * </ol>
     */
    private static final Pattern PATTERN_NULL_NOT_ALLOWED_INFO = Pattern.compile(
            "NULL\\snot\\sallowed\\sfor\\scolumn\\s\"(.*)\";\\sSQL\\sstatement:\\s(?:.*)into\\s([^\\s]*)\\s",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNICODE_CASE);


    /**
     * Шаблон выделения информации из сообщения об ошибке при срабатывании ограничения на CHECK.
     * <p/>
     * Выделяются следующие группы:
     * <ol>
     * <li>Имя ограничения.</li>
     * <li>Наименование таблицы.</li>
     * </ol>
     */
    private static final Pattern PATTERN_CHECK_CONSTRAINT_INFO = Pattern.compile(
            "Check\\sconstraint\\sviolation:\\s\"([^\\s]*):\\s(?:.*)\";\\sSQL\\sstatement:\\s(?:.*)into\\s([^\\s]*)\\s",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNICODE_CASE);


    /**
     * Шаблон выделения информации из сообщения об ошибке при срабатывании проверки ссылочной целостности.
     * <p/>
     * Выделяются следующие группы:
     * <ol>
     * <li>Имя ограничения.</li>
     * <li>Наименование таблицы.</li>
     * </ol>
     */

    private static final Pattern PATTERN_REFERENTIAL_INTEGRITY_VIOLATED_INFO = Pattern.compile(
            "Referential\\sintegrity\\sconstraint\\sviolation:\\s\"([^\\s]*):\\sPUBLIC\\.([^\\s]*)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNICODE_CASE);

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(H2DbExceptionParser.class);


    @Override
    public DbExceptionDescriptor parse(Throwable throwable) {
        if (throwable instanceof JdbcSQLException) {
            final JdbcSQLException jdbcException = (JdbcSQLException) throwable;
            final int errorCode = jdbcException.getErrorCode();
            if (ErrorCode.DUPLICATE_KEY_1 == errorCode) {
                return processDuplicateKeyException(jdbcException);
            } else if (ErrorCode.NULL_NOT_ALLOWED == errorCode) {
                return processNullNotAllowedException(jdbcException);
            } else if (ErrorCode.CHECK_CONSTRAINT_VIOLATED_1 == errorCode) {
                return processCheckConstraintException(jdbcException);
            } else if (ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_CHILD_EXISTS_1 == errorCode) {
                return processReferentialIntegrityViolatedException(jdbcException);

            }
        }
        return null;
    }

    /**
     * Производит обработку исключения о нарушении констрейнта CHECK.
     *
     * @param exception обрабатываемое исключение
     * @return новое исключение, сформированное в результате обработки, либо null, если его обработать не удалось
     */
    @Nullable
    private DbExceptionDescriptor processCheckConstraintException(SQLException exception) {
        final Matcher matcher = PATTERN_CHECK_CONSTRAINT_INFO.matcher(exception.getMessage());
        if (!matcher.find() || matcher.groupCount() != 2) {
            LOG.warn("Unable to parse exception", exception);
            return null;
        }
        final String constraint = matcher.group(1);
        final String table = matcher.group(2);

        DbExceptionDescriptorImpl dbExceptionDescriptor = new DbExceptionDescriptorImpl();
        dbExceptionDescriptor.setCause(exception);
        dbExceptionDescriptor.setDetails(exception.getMessage());
        dbExceptionDescriptor.setConstraint(constraint);
        dbExceptionDescriptor.setTableName(table);
        dbExceptionDescriptor.setXfwViolationType(XfwViolationType.Check);

        return dbExceptionDescriptor;
    }


    /**
     * Производит обработку исключения о провале проверки на дублирующиеся значения.
     *
     * @param exception обрабатываемое исключение
     * @return новое исключение, сформированное в результате обработки, либо null, если его обработать  не удалось
     */
    @Nullable
    private DbExceptionDescriptor processDuplicateKeyException(final SQLException exception) {
        final Matcher matcher = PATTERN_DUPLICATE_KEY_INFO.matcher(exception.getMessage());
        if (!matcher.find() || matcher.groupCount() != 3) {
            LOG.warn("Unable to parse exception", exception);
            return null;
        }

        final String constraint = matcher.group(1);
        final String table = matcher.group(2);
        final String columnsString = matcher.group(3);

        DbExceptionDescriptorImpl dbExceptionDescriptor = new DbExceptionDescriptorImpl();
        dbExceptionDescriptor.setCause(exception);
        dbExceptionDescriptor.setDetails(exception.getMessage());
        dbExceptionDescriptor.setConstraint(constraint);
        dbExceptionDescriptor.setTableName(table);
        dbExceptionDescriptor.setXfwViolationType(XfwViolationType.Unique);
        dbExceptionDescriptor.setColumnNames(Arrays.stream(split(columnsString, ','))
                .map(StringUtils::trim).collect(Collectors.toList()));


        return dbExceptionDescriptor;
    }

    /**
     * Производит обработку исключения о провале проверки на заполненность обязательного поля.
     *
     * @param exception обрабатываемое исключение
     * @return новое исключение, сформированное в результате обработки, либо null, если его обработать не удалось
     */
    @Nullable
    private DbExceptionDescriptor processNullNotAllowedException(final SQLException exception) {
        final Matcher matcher = PATTERN_NULL_NOT_ALLOWED_INFO.matcher(exception.getMessage());
        if (!matcher.find() || matcher.groupCount() != 2) {
            LOG.warn("Unable to parse exception", exception);
            return null;
        }
        final String table = matcher.group(2);
        final String columnsString = matcher.group(1);

        DbExceptionDescriptorImpl dbExceptionDescriptor = new DbExceptionDescriptorImpl();
        dbExceptionDescriptor.setCause(exception);
        dbExceptionDescriptor.setDetails(exception.getMessage());
        dbExceptionDescriptor.setTableName(table);
        dbExceptionDescriptor.setXfwViolationType(XfwViolationType.NotNull);
        dbExceptionDescriptor.setColumnNames(Arrays.stream(split(columnsString, ','))
                .map(StringUtils::trim).collect(Collectors.toList()));


        return dbExceptionDescriptor;
    }


    /**
     * Производит обработку исключения о нарушении ссылочной целостности.
     *
     * @param exception обрабатываемое исключение
     * @return новое исключение, сформированное в результате обработки, либо null, если его обработать  не удалось
     */
    @Nullable
    private DbExceptionDescriptor processReferentialIntegrityViolatedException(final SQLException exception) {
        final Matcher matcher = PATTERN_REFERENTIAL_INTEGRITY_VIOLATED_INFO.matcher(exception.getMessage());
        if (!matcher.find() || matcher.groupCount() != 2) {
            LOG.warn("Unable to parse exception", exception);
            return null;
        }

        final String constraint = matcher.group(1);
        final String table = matcher.group(2);

        DbExceptionDescriptorImpl dbExceptionDescriptor = new DbExceptionDescriptorImpl();
        dbExceptionDescriptor.setCause(exception);
        dbExceptionDescriptor.setDetails(exception.getMessage());
        dbExceptionDescriptor.setConstraint(constraint);
        dbExceptionDescriptor.setTableName(table);
        dbExceptionDescriptor.setXfwViolationType(XfwViolationType.ReferenceViolation);


        return dbExceptionDescriptor;


    }

}
