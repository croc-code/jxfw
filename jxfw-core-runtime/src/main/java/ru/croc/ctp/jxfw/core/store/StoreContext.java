package ru.croc.ctp.jxfw.core.store;

import com.google.common.collect.Lists;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Контекст сохранения.
 *
 * @author AKogun
 * @since 1.5
 */
public class StoreContext {

    private String txId = "empty";
    private Locale locale = Locale.getDefault();
    private TimeZone timeZone = TimeZone.getDefault();
    private List<String> hints = Collections.EMPTY_LIST;
    private Principal principal;
    private StoreResult storeResult;
    /**
     * Временные объекты, которые доступны между шагами обработки контекста,
     * доступ осуществляется по ключу, в хранилище не сохраняются.
     */
    private final Map<String, Object> commonObjects = new HashMap<>();

    /**
     * Ключи для дополнительного хранения объектов контекста в {@link StoreContext#getCommonObjects()}.
     */
    @Deprecated
    interface StoreContextKeys {

        /**
         * Хинты.
         */
        String HINTS_KEY = "store_context.hints";
        /**
         * Локаль.
         */
        String LOCALE_KEY = "store_context.locale";
        /**
         * TxId.
         */
        String TX_KEY = "store_context.tx";

    }

    /**
     * Список DomainTo объектов на основе которых был создан контекст сохранения.
     */
    private final List<DomainTo> originalObjects;

    /**
     * Список доменных объектов, которые будут сохранены в рамках обработки контекста сохранения.
     */
    private final List<? extends DomainObject<?>> domainObjects;


    /**
     * Конструктор.
     *
     * @param uow unitOfWork
     */
    public StoreContext(Iterable<DomainTo> uow) {
        this(uow, null);
    }

    /**
     * Конструктор.
     */
    protected StoreContext() {
        this(null, null);
    }

    /**
     * Конструктор.
     *
     * @param originalObjects UoW(список DTO).
     * @param domainObjects   UoW(список доменных объектов).
     */
    protected StoreContext(Iterable<DomainTo> originalObjects, Iterable<? extends DomainObject<?>> domainObjects) {
        if (originalObjects == null) {
            this.originalObjects = Lists.newArrayList();
        } else {
            this.originalObjects = Lists.newArrayList(originalObjects);
        }
        if (domainObjects == null) {
            this.domainObjects = Lists.newArrayList();
        } else {
            this.domainObjects = Lists.newArrayList(domainObjects);
        }
    }

    /**
     * Конструктор.
     *
     * @param storeContextBuilder {@link StoreContextBuilder}
     */
    public StoreContext(StoreContextBuilder storeContextBuilder) {
        if (storeContextBuilder != null) {
            originalObjects = storeContextBuilder.originalObjects;
            domainObjects = storeContextBuilder.domainObjects;
            setHints(storeContextBuilder.hints);
            setLocale(storeContextBuilder.locale);
            setTimeZone(storeContextBuilder.timeZone);
            setPrincipal(storeContextBuilder.principal);
            setTxId(storeContextBuilder.txId);

        } else {
            originalObjects = Lists.newArrayList();
            domainObjects = Lists.newArrayList();
        }
    }

    /**
     * Добавление доменного объекта в контекст сохранения, если не был добавлен
     * ранее.
     *
     * @param domainObject - доменный объект
     * @param <T>          - тип
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainObject<?>> void add(T domainObject) {
        if (!domainObjects.contains(domainObject)) {
            ((List<? super DomainObject<?>>) domainObjects).add(domainObject);
        }
    }

    /**
     * Добавление доменных объектов в контекст сохранения. Объекты добавляются, если
     * не содержатся в контексте сохранения. Проверяется каждый объект.
     *
     * @param domainObjects - доменные объекты
     */
    public void addAll(Iterable<? extends DomainObject<?>> domainObjects) {
        for (DomainObject<?> domainObject : domainObjects) {
            add(domainObject);
        }
    }

    /**
     * Очистить список доменных объектов.
     */
    public void removeAll() {
        domainObjects.clear();
    }

    public List<? extends DomainObject<?>> getDomainObjects() {
        return domainObjects;
    }

    public List<DomainTo> getOriginalsObjects() {
        return originalObjects;
    }


    /**
     * Статический метод для получения StoreContext из списка DTO.
     *
     * @param uow UoW(список DTO).
     * @return StoreContext
     */
    public static StoreContext from(List<DomainTo> uow) {
        return new StoreContext(uow);
    }

    /**
     * Статический метод для получения StoreContext из списка доменных объектов.
     *
     * @param uow UoW(список доменных объектов).
     * @return StoreContext
     */
    public static StoreContext fromDomainObjects(List<? extends DomainObject<?>> uow) {
        return new StoreContext(null, uow);
    }

    public Map<String, Object> getCommonObjects() {
        return commonObjects;
    }

    public List<String> getHints() {
        return hints;
    }

    /**
     * Установить хинты.
     *
     * @param hints хинты
     */
    public void setHints(List<String> hints) {
        //проекты на версиях до версии 1.6 хранили в commonObjects.
        getCommonObjects().put(StoreContext.StoreContextKeys.HINTS_KEY, hints);
        this.hints = hints;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        //НЕ добавляем StoreContext.getCommonObjects (они там не хранились,
        // старые значения оставляю ru.croc.ctp.jxfw.core.store.StoreContext.StoreContextKeys)
        this.timeZone = timeZone;
    }

    /**
     * Получить значение {@link Locale}.
     *
     * @return Значение, установленное ранее, в рамках конвейера сохранения получается из запроса сохранения, если
     * значение не было установлено, возвращается значение {@link Locale} по умолчанию.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Установить локаль.
     *
     * @param locale локаль
     */
    public void setLocale(Locale locale) {
        //проекты на версиях до версии 1.6 хранили в commonObjects.
        getCommonObjects().put(StoreContext.StoreContextKeys.LOCALE_KEY, locale);
        this.locale = locale;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        //НЕ добавляем StoreContext.getCommonObjects (они там не хранились,
        // старые значения оставляю ru.croc.ctp.jxfw.core.store.StoreContext.StoreContextKeys)
        this.principal = principal;
    }

    public String getTxId() {
        return txId;
    }


    /**
     * Установить txId.
     *
     * @param txId txId
     */
    public void setTxId(String txId) {
        //проекты на версиях до версии 1.6 хранили в commonObjects.
        getCommonObjects().put(StoreContext.StoreContextKeys.TX_KEY, txId);
        this.txId = txId;
    }

    public void setStoreResult(StoreResult storeResult) {
        this.storeResult = storeResult;
    }

    /**
     * Результат сохранения UoW.
     *
     * @return StoreResult
     */
    public StoreResult getStoreResult() {
        return storeResult;
    }

    /**
     * Билдер контекста {@link StoreContext}.
     */
    public static class StoreContextBuilder {
        private List<String> hints;
        private TimeZone timeZone;
        private Locale locale;
        private Principal principal;
        private String txId;

        /**
         * Установить хинты.
         *
         * @param hints хинты
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withHints(List<String> hints) {
            this.hints = hints;
            return this;
        }

        /**
         * Установить временную зону.
         *
         * @param timeZone временная зона
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        /**
         * Установить локаль.
         *
         * @param locale локаль
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Установить принципала.
         *
         * @param principal принципал
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withPrincipal(Principal principal) {
            this.principal = principal;
            return this;
        }

        /**
         * Установить txId.
         *
         * @param txId txId
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withTxId(String txId) {
            this.txId = txId;
            return this;
        }

        private List<DomainTo> originalObjects = Lists.newArrayList();
        private List<? extends DomainObject<?>> domainObjects = Lists.newArrayList();

        /**
         * Со списком DTO.
         *
         * @param uow UoW(список DTO).
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withUow(List<DomainTo> uow) {
            if (uow != null) {
                originalObjects.addAll(uow);
            }
            return this;
        }

        /**
         * Со списком доменных объектов.
         *
         * @param domainObjectItems Список доменных объектов
         * @return {@link StoreContextBuilder}
         */
        public StoreContextBuilder withDomainObjects(List<? extends DomainObject<?>> domainObjectItems) {
            if (domainObjectItems != null) {
                domainObjects = Lists.newArrayList(domainObjectItems);
            }
            return this;
        }

        /**
         * Собрать контекст.
         * @return {@link StoreContext}
         */
        public StoreContext build() {
            return new StoreContext(this);
        }
    }
}