package ru.croc.ctp.jxfw.core.facade.webclient;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;

/**
 * Фабрика для временных доменных объектов.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class TransientDomainObjectFactory extends AbstractDomainObjectFactory {
    /**
     * Фабрика для временных доменных объектов.
     *
     * @param context         контест конвертации объектов.
     * @param domainToServicesResolver - сервис для получения бинов To-сервисов доменных моделей.
     */
    public TransientDomainObjectFactory(ConvertContext context, DomainToServicesResolver domainToServicesResolver) {
        super(context, domainToServicesResolver);
    }

    @Override
    public <T extends DomainObject<?>> T create(DomainToService<T, ?> toService, DomainTo dto) {
        T result = super.create(toService, dto);
        if (result != null) {
            return result;
        }

        result = toService.createNewDomainObject(dto.getId());
        result.setRemoved(dto.isRemoved());
        if (context != null) {
            context.objects.add(result);
        }
        return result;
    }


}
