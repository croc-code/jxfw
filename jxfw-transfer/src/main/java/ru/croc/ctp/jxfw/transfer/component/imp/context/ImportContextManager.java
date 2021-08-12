package ru.croc.ctp.jxfw.transfer.component.imp.context;


import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;

/**
 * Управляет жизненым циклом контекстов импорта. Обеспечивает использования одного и того же контекста
 * для различных операций с одним ресурсом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public interface ImportContextManager {
    /** Возращает контекст для текущего ресурса.
     * @param resourceId ид ресурса.
     * @param dependencyManager компонент проверяющий корректность загружаемых объектов.
     * @param splitter компонент для разбиения объектов по группам.
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа, иначе падаем.
     * @param ignoreBidirectional игнорировать ли обратные ссылки.
     * @param domainFacadeIgnoreService сервис определяющий игнорируемые фасадом поля.
     * @return формированный контекст импорта.
     * @throws Exception если в процессе загрузки контекста произошла ошибка.
     */
    ImportContext getImportContext(String resourceId,
                                   ImportDependencyManager dependencyManager,
                                   ImportContextSplitterAndAggregator splitter,
                                   boolean isIgnoreObjectsOfUnknownType,
                                   boolean ignoreBidirectional,
                                   DomainFacadeIgnoreService domainFacadeIgnoreService) throws Exception;

    /** Проверяет используется ли контекст.
     * @param resourceId идентификатор ресурса.
     * @return true если контекст используется, иначе false.
     */
    boolean isUsingContext(String resourceId);
}
