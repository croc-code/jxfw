package ru.croc.ctp.jxfw.core.load;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Базовая реализация {@link LoadService}.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public abstract class LoadServiceSupport implements LoadService {

    @Nonnull
    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> T loadOne(QueryParams<T, ID> queryParams,
                                                                           LoadContext<T> loadContext) throws IllegalStateException {
        //TODO:
        return load(queryParams, loadContext).stream().reduce((item1, item2) -> {
            throw new IllegalStateException(
                    MessageFormat.format("Only one result is expected (params: {0})", this));
        }).orElseThrow(() ->
                new IllegalStateException(MessageFormat.format("No results was found (params: {0})", this)));
    }

    @Nonnull
    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> Optional<T> loadOptional(
            @Nonnull QueryParams<T, ID> queryParams,
            LoadContext<T> loadContext)
            throws IllegalStateException {


        try {
            return load(queryParams, loadContext).stream().reduce((item1, item2) -> {
                throw new IllegalStateException(
                        MessageFormat.format("Only one result is expected (params: {0})", this));
            });
        } catch (final XObjectNotFoundException exception) {
            return Optional.empty();
        }

    }
}
