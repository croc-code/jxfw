package ru.croc.ctp.jxfw.core.load;

import java.util.List;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

/**
 * Сервис загрузки дополнительных данных.
 * 
 * @since 1.3
 * 
 * @author AKogun
 */
public interface PreloadService {
    
    /**
     * Загрузка дополнительных данных.
     *
     * @param objects - объекты для которых необходимо получить данные
     * @param preLoadProps - наименования свойств, которые необходимо дозагрузить
     * @return список объектов, которые были дозагружены
     */
    List<? extends DomainObject<?>> loadMoreFor(Iterable<? extends DomainObject<?>> objects,
                               List<String> preLoadProps);

}
