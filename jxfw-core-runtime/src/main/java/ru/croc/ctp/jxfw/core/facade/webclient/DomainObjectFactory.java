package ru.croc.ctp.jxfw.core.facade.webclient;


import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.context.LoadContextWithStoreContext;
import ru.croc.ctp.jxfw.core.store.context.StoreContextKeys;

/**
 * Фабрика для доменных объектов.
 *
 * @since 1.0
 */
public class DomainObjectFactory extends AbstractDomainObjectFactory {
    /**
     * Фабрика для доменных объектов.
     *
     * @param domainToServicesResolver сервис для получения бинов To-сервисов доменных моделей.
     * @deprecated since 1.6
     */
    @Deprecated
    public DomainObjectFactory(DomainToServicesResolver domainToServicesResolver) {
        this(null, domainToServicesResolver);
    }

    /**
     * Фабрика для доменных объектов.
     *
     * @param context         контест конвертации объектов. Обхект типа {@link ConvertContext},
     *                        который в себе содержит списки объектов в виде TO объектов и
     *                        преобразованных в доменные объекты.
     * @param domainToServicesResolver - сервис для получения бинов To-сервисов доменных моделей.
     */
    public DomainObjectFactory(ConvertContext context, DomainToServicesResolver domainToServicesResolver) {
        super(context, domainToServicesResolver);
    }

    /**
     * Создать объект по его ИД и имени доменного Типа.
     *
     * @param id   - ИД объекта.
     * @param type - имя доменного типа объекта.
     * @return объект, полученый либо из контекста конвертации, если был передан с клиента в UoW,
     *         либо получен из БД, если на этот объект есть ссылки из объектов в UoW.
     */
    @Override
    public <T extends DomainObject<?>> T create(String id, String type) {
        T result =  super.create(id, type);
        if (result != null) {
            return result;
        }
        /*
        * TODO Сейчас по-факту, если мы не нашли нигде элемент, то пытаемся
        * найти его в БД. TODO Для больших списков будет выполнен запрос по
        * каждому элементу, необходимо решить эту проблему. TODO Пока
        * рекоммендуется использовать двунаправленные связи при проектировании
        * модели
        */
        
        result = getDomainObjectById(domainToServicesResolver.resolveToService(type), id);
        if (context != null && result != null) {
        	context.objects.add(result);
        }
        
        return result;
    }

    /**
     * Создать доменный объект по DomainTO.
     * 
     * @param toService - DomainTo сервис 
     * @param dto - DomaintTo объект
     * @return объект из контекста конвертации либо из хранилища
     */
    @Override
    @SuppressWarnings("unchecked")
	public <T extends DomainObject<?>> T create(final DomainToService<T, ?> toService, final DomainTo dto) {
		T result = super.create(toService, dto);
		if (result != null) {
			return result;
		}
		
		if (dto.isNew()) {
			result = toService.createNewDomainObject(dto.getId());
		} else {
			result = getDomainObjectById(toService, dto.getId());
		}
		result.setRemoved(dto.isRemoved());
		if (context != null) {
            context.objects.add(result);
        }
		return result;
	}

    @SuppressWarnings("unchecked")
    private <T extends DomainObject<?>> T getDomainObjectById(final DomainToService<T, ?> toService, final String key) {
        boolean existsStoreContext = context != null && context.storeContext != null;
        LoadContext.Builder<T> loadContextBuilder = existsStoreContext
                ? new LoadContextWithStoreContext.Builder(context.storeContext)
                : new LoadContext.Builder<>();

        LoadContext<T> loadContext = loadContextBuilder.build();
        T domainObject = toService.getDomainObjectById(key, loadContext);
        if (existsStoreContext && !loadContext.getCommonObjects().isEmpty()) {
            context.storeContext.getCommonObjects()
                    .put(StoreContextKeys.LOAD_CONTEXT_COMMON_OBJECT, loadContext.getCommonObjects());
        }

        return domainObject;
    }
}
