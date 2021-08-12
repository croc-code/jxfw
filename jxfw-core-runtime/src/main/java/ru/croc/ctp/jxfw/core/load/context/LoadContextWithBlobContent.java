package ru.croc.ctp.jxfw.core.load.context;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;

import java.io.Serializable;

/**
 * Контекст загрузки содержащий информацию по бинарному контенту.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public class LoadContextWithBlobContent<T extends DomainObject<? extends Serializable>> extends LoadContextDelegate<T> {

    /**
     * Создает {@link LoadContextDelegate}.
     *
     * @param loadContext оборачиваемый контекст
     */
    private LoadContextWithBlobContent(final LoadContext<T> loadContext) {
        super(loadContext);
    }

    /**
     * Помещает имя бинарного свойства в {@link LoadContext}.
     *
     * @param name
     *        имя бинарного свойства
     * @return {@link LoadContextWithBlobContent}
     */
    public LoadContextWithBlobContent<T> putBlobContentName(String name) {
        getCommonObjects().put(LoadContextKeys.BLOB_CONTENT, name);
        return this;
    }

    /**
     * Получает имя бинарного свойства из {@link LoadContext}.
     *
     * @return имя бинарного свойства.
     */
    public String getBlobContentName() {
        return (String) this.getCommonObjects().get(LoadContextKeys.BLOB_CONTENT);
    }

    /**
     * Проверяет производится ли загрузка свойства бинарного контента.
     *
     * @return {@code true} в случае загрузки.
     */
    public boolean isBlobContent() {
        return this.getCommonObjects().containsKey(LoadContextKeys.BLOB_CONTENT);
    }

    /**
     * Получить обертку {@link LoadContextWithBlobContent} над другим контекстом.
     *
     * @param loadContext контекстом
     * @return {@link LoadContextWithBlobContent}
     */
    public static <T extends DomainObject<? extends Serializable>> LoadContextWithBlobContent<T> from(LoadContext<T> loadContext) {
        return new LoadContextWithBlobContent<>(loadContext);
    }
}
