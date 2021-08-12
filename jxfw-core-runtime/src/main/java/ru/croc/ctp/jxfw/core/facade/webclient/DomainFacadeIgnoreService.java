package ru.croc.ctp.jxfw.core.facade.webclient;


import ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore;
import ru.croc.ctp.jxfw.core.domain.meta.XFWServerOnly;

import java.util.List;

/**
 * Сервис позволяющий определить включенно ли игнорирование
 * для указанной связки фасада и типа доменного объекта.
 *
 * @author Alexander Golovin
 * @see XFWFacadeIgnore
 * @see XFWServerOnly
 * @since 1.6
 */
public interface DomainFacadeIgnoreService {
    /**
     * Определяет включенно ли игнорирование указанного типа объекта фасадом.
     *
     * @param typeName название типа объекта
     * @param facadeName название фасада
     * @return true если включено игнорирование, иначе false
     */
    boolean isIgnore(String typeName, String facadeName);

    /**
     * Определяет включенно ли игнорирование поля указанного типа объекта фасадом.
     *
     * @param typeName название типа объекта
     * @param fieldName название поля
     * @param facadeName название фасада
     * @return true если включено игнорирование, иначе false
     */
    boolean isIgnore(String typeName, String fieldName, String facadeName);

    /**
     * Возвращает имена полей у типа доменного объекта, которые должны игнорироваться для указанного фасада.
     *
     * @param typeName название типа объекта
     * @param facadeName название фасада
     * @return имена игнорируемых полей.
     */
    List<String> getIgnoredFields(String typeName, String facadeName);
}
