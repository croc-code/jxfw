package ru.croc.ctp.jxfw.core.datasource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.QTuple;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Результат выполнения запроса к источнику данных.
 * Обертка вокруг {@code List<Tuples>} для дальнейшей сериализации в JSON.
 * @deprecated since 1.6
 */
@Deprecated
public final class QTupleDataSourceResult implements DataSourceResult<Tuple> {

    private List<Tuple> tuples;

    private List<Tuple> more;

    private QTuple qtuple = null;

    private Map<String, Object> hints;

    /**
     * Конструктор.
     *
     * @param tuples - результаты
     */
    protected QTupleDataSourceResult(Iterable<Tuple> tuples) {
        this.tuples = Lists.newArrayList(tuples);
    }

    /**
     * Конструктор.
     *
     * @param tuples - результаты
     * @param more   - доп. результаты
     * @param hints  - хинты для отображения
     */
    protected QTupleDataSourceResult(Iterable<Tuple> tuples, Iterable<Tuple> more, Map<String, Object> hints) {
        this.tuples = Lists.newArrayList(tuples);
        this.more = Lists.newArrayList(more);
        this.hints = Maps.newHashMap(hints);
    }

    /**
     * {@inheritDoc}.
     */
    public List<Tuple> getData() {
        return tuples;
    }

    @Override
    public List<Tuple> getMore() {
        return more;
    }

    @Override
    public Map<String, Object> getHints() {
        return hints;
    }

    /**
     * Получение через Reflection API внутреннего поля this$0.
     * Нужно для корректной сериализации в JSON.
     * 
     * @return значение поля this$0.
     */
    public QTuple getQTuple() {
        if (qtuple == null) {
            try {
                Tuple tuple = tuples.get(0);
                Field field = tuple.getClass().getDeclaredField("this$0");
                field.setAccessible(true);
                qtuple = (QTuple) field.get(tuple);
            } catch (IllegalArgumentException | IllegalAccessException
                    | NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return qtuple;
    }

}
