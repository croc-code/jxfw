package ru.croc.ctp.jxfw.core.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Результат загрузки произвольных данных.
 * @param <T> тип загружаемых данных
 * @param <U> тип дополнительных данных
 *
 * @since 1.6
 * @author OKrutova
 */
public class GeneralLoadResult<T, U> {

    private final List<T> data;
    private final List<U> moreList;
    private final Map<String, Object> hints;

    /**
     * Конструтор.
     */
    public GeneralLoadResult() {
        data = new ArrayList<>();
        moreList = new ArrayList<>();
        hints = new HashMap<>();
    }

    /**
     * Конструтор.
     * @param data список основных загружаемых объектов.
     * @param moreList список дополнительных загружаемых объектов.
     * @param hints хинты, возвращаемые на клиент вместе с результатом.
     */
    public GeneralLoadResult(@Nonnull List<T> data, @Nonnull List<U> moreList, @Nonnull Map<String, Object> hints) {
        this.data = data;
        this.moreList = moreList;
        this.hints = hints;
    }

    /**
     * Конструтор.
     * @param data список доменных объектов.
     */
    public GeneralLoadResult(@Nonnull List<T> data) {
        this();
        this.data.addAll(data);
    }

    /**
     * Конструтор.
     * @param data список доменных объектов.
     * @param hints хинты, возвращаемые на клиент вместе с результатом.
     */
    public GeneralLoadResult(@Nonnull List<T> data, @Nonnull Map<String, Object> hints) {
        this();
        this.data.addAll(data);
        this.hints.putAll(hints);
    }

    @Nonnull
    public List<T> getData() {
        return data;
    }

    @Nonnull
    public List<U> getMoreList() {
        return moreList;
    }

    @Nonnull
    public Map<String, Object> getHints() {
        return hints;
    }
}
