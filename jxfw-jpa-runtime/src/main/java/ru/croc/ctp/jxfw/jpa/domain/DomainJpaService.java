package ru.croc.ctp.jxfw.jpa.domain;

import com.querydsl.jpa.impl.JPAQuery;
import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.util.List;

/**
 * Интерфейс Spring-сервисов для доменных моделей модуля Jpa.
 *
 * @since 1.10
 */
public interface DomainJpaService {
    /**
     * Загрузка навигируемых свойств, предполагается рекурсивный вызов метода.
     * @param query Запрос для загрузки свойств уровня выше.
     * @param preloads Список свойств которые необходимо прогрузить для текущего уровня и ниже.
     * @param index Индекс для формирования alias-ов сущностей в запросе.
     * @return загруженные объекты навигируемых свойств.
     */
    List<DomainObject<?>> computePreload(
        final JPAQuery<? extends DomainObject<?>> query, final List<List<String>> preloads, int index
    );
}
