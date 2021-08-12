package ru.croc.ctp.jxfw.transfer.component.imp;

import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportDependencyManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;

/**
 * Создаёт {@link ImportGroupReader}.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportGroupReaderFactory {
    /**
     * Создаёт {@link ImportGroupReader} для чтения групп объектов из файла.
     *
     * @param importContextManager контейнер контекстов имопрта
     * @param loaderFactory фабрика загрузчиков данных из файла определенного формата
     * @param transferContextService сервис для работы с контекстом
     * @param dependencyManager компонент проверяющий корректность загружаемых объектов
     * @param splitter компонент для разбиения объектов по группам
     * @param handler обработчик объетков с неразрешенными зависимостями
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа
     * @return новый {@link ImportGroupReader}.
     * @throws IllegalArgumentException если любой из параметров null.
     */
    ImportGroupReader create(ImportContextManager importContextManager,
                             FileDtoReaderFactory loaderFactory,
                             TransferContextService transferContextService,
                             ImportDependencyManager dependencyManager,
                             ImportContextSplitterAndAggregator splitter,
                             ImportDependencyCollisionHandler handler,
                             boolean isIgnoreObjectsOfUnknownType) throws IllegalArgumentException;

    /**
     * Создаёт {@link ImportGroupReader} для чтения групп объектов из файла.
     *
     * @param importContextManager контейнер контекстов имопрта
     * @param loaderFactory фабрика загрузчиков данных из файла определенного формата
     * @param transferContextService сервис для работы с контекстом
     * @param dependencyManager компонент проверяющий корректность загружаемых объектов
     * @param splitter компонент для разбиения объектов по группам
     * @param handler обработчик объетков с неразрешенными зависимостями
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа
     * @param ignoreBidirectional игнорировать ли обратные ссылки
     * @return новый {@link ImportGroupReader}.
     * @throws IllegalArgumentException если любой из параметров null.
     */
    ImportGroupReader create(ImportContextManager importContextManager,
                             FileDtoReaderFactory loaderFactory,
                             TransferContextService transferContextService,
                             ImportDependencyManager dependencyManager,
                             ImportContextSplitterAndAggregator splitter,
                             ImportDependencyCollisionHandler handler,
                             boolean isIgnoreObjectsOfUnknownType,
                             boolean ignoreBidirectional) throws IllegalArgumentException;
}
