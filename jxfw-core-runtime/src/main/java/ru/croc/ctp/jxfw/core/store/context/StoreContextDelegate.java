package ru.croc.ctp.jxfw.core.store.context;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.StoreResult;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Базовый класс для расширения функций контекста сохранения.
 *
 * @author AKogun
 * @since 1.6
 */
public abstract class StoreContextDelegate extends StoreContext {

    private final StoreContext storeContext;

    /**
     * Конструктор.
     *
     * @param storeContext контекст сохранения.
     */
    protected StoreContextDelegate(final StoreContext storeContext) {
        this.storeContext = storeContext;
    }

    @Override
    public <T extends DomainObject<?>> void add(T domainObject) {
        this.storeContext.add(domainObject);
    }

    @Override
    public void addAll(Iterable<? extends DomainObject<?>> domainObjects) {
        this.storeContext.addAll(domainObjects);
    }

    @Override
    public void removeAll() {
        this.storeContext.removeAll();
    }

    @Override
    public List<? extends DomainObject<?>> getDomainObjects() {
        return this.storeContext.getDomainObjects();
    }

    @Override
    public List<DomainTo> getOriginalsObjects() {
        return this.storeContext.getOriginalsObjects();
    }

    @Override
    public void setStoreResult(StoreResult storeResult) {
        this.storeContext.setStoreResult(storeResult);
    }

    @Override
    public StoreResult getStoreResult() {
        return this.storeContext.getStoreResult();
    }

    @Override
    public Map<String, Object> getCommonObjects() {
        return this.storeContext.getCommonObjects();
    }

    @Override
    public List<String> getHints() {
        return this.storeContext.getHints();
    }

    @Override
    public void setHints(List<String> hints) {
        this.storeContext.setHints(hints);
    }

    @Override
    public Locale getLocale() {
        return this.storeContext.getLocale();
    }

    @Override
    public void setLocale(Locale locale) {
        this.storeContext.setLocale(locale);
    }

    @Override
    public Principal getPrincipal() {
        return this.storeContext.getPrincipal();
    }

    @Override
    public void setPrincipal(Principal principal) {
        this.storeContext.setPrincipal(principal);
    }

    @Override
    public String getTxId() {
        return this.storeContext.getTxId();
    }

    @Override
    public void setTxId(String txId) {
        this.storeContext.setTxId(txId);
    }

    @Override
    public TimeZone getTimeZone() {
        return this.storeContext.getTimeZone();
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        this.storeContext.setTimeZone(timeZone);
    }
}
