package ru.croc.ctp.jxfw.core.load.context;

import java8.util.Optional;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.GeneralLoadResult;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nonnull;

/**
 * Базовый класс для расширения функций контекста загрузки.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public class LoadContextDelegate<T extends DomainObject<? extends Serializable>> extends LoadContext<T> {

    private final LoadContext<T> loadContext;

    /**
     * Создает {@link LoadContextDelegate}.
     *
     * @param loadContext
     *        оборачиваемый контекст.
     */
    protected LoadContextDelegate(final LoadContext<T> loadContext) {
        this.loadContext = loadContext;
    }

    @Override
    public LoadResult<T> getLoadResult() {
        return loadContext.getLoadResult();
    }

    @Override
    @Nonnull
    public Locale getLocale() {
        return loadContext.getLocale();
    }

    @Override
    @Nonnull
    public TimeZone getTimeZone() {
        return loadContext.getTimeZone();
    }

    @Override
    @Nonnull
    public List<String> getHints() {
        return loadContext.getHints();
    }

    @Override
    public Optional<Principal> getPrincipal() {
        return loadContext.getPrincipal();
    }

    @Override
    @Nonnull
    public Map<String, Object> getCommonObjects() {
        return loadContext.getCommonObjects();
    }

    @Override
    public void setLoadResult(GeneralLoadResult<T, DomainObject<? extends Serializable>> loadResult) {
        loadContext.setLoadResult(loadResult);
    }
}
