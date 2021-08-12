package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore;
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportException;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Анализирует содержимое файла с доменными объектами и строит контекст импорта {@link DefaultImportContext}
 * на основе зависимостей между объектами. В зависимости от конфигурации формат файла может отличаться.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public final class DefaultFileScanner {
    private static final Logger log = LoggerFactory.getLogger(DefaultFileScanner.class);
    private static final String FACADE_NAME = "transfer";
    /** Фабрика для создания парсера файла. */
    private FileDtoReaderFactory factoryOfReader;
    private DefaultImportContext context;
    private DomainServicesResolver resolver;
    private DomainFacadeIgnoreService domainFacadeIgnoreService;
    private File file;
    /** Указывает игнорировать ли объекты неизвестного типа. Иначе падаем. */
    private boolean isIgnoreObjectsOfUnknownType;
    /** Игнорировать ли обратные ссылки. */
    private boolean ignoreBidirectional;
    /** Метамодель. */
    private final XfwModel xfwModel;


    /** Создает анализатор файла для однаразового использования.
     * @param resolver сопоставитель типов xml к java.
     * @param factoryOfReader фабрика парсера для четния доменных объектов.
     * @param context контехт импорта для заполнения.
     * @param file файл по которому будет проводится анализ.
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа, иначе падаем.
     * @param ignoreBidirectional игнорировать ли обратные ссылки.
     * @param domainFacadeIgnoreService сервис определяющий игнорируемые фасадом поля.
     */
    public DefaultFileScanner(DomainServicesResolver resolver, FileDtoReaderFactory factoryOfReader,
                              DefaultImportContext context, File file, boolean isIgnoreObjectsOfUnknownType,
                              boolean ignoreBidirectional, DomainFacadeIgnoreService domainFacadeIgnoreService) {
        this.resolver = resolver;
        this.factoryOfReader = factoryOfReader;
        this.context = context;
        this.file = file;
        this.isIgnoreObjectsOfUnknownType = isIgnoreObjectsOfUnknownType;
        this.ignoreBidirectional = ignoreBidirectional;
        this.xfwModel = XfwModelFactory.getInstance();
        this.domainFacadeIgnoreService = domainFacadeIgnoreService;
        ImportContextUtils.clear(context);
    }

    /** Анализирует содержимое файла с доменными объектами и строит контекст импорта {@link DefaultImportContext}
     *  на основе зависимостей между объектами.
     *
     *  @throws ImportParseException проблемы c форматом документа.
     *  @throws IOException проблемы с чтением файла.
     *  @throws ImportException если возникла проблема при импорте.
     */
    public void scan() throws ImportParseException, IOException, ImportException {
        scanFile();
    }


    /** Читает полседовательно все доменные объекты из источника и заносит информацию о них в контекст.
     *
     *  @throws ImportParseException проблемы c форматом документа.
     *  @throws IOException проблемы с чтением файла.
     *  @throws ImportException если возникла проблема при импорте.
     */
    private void scanFile() throws ImportParseException,
            IOException, ImportException {
        try (FileDtoReader reader = factoryOfReader.createReader(file)) {
            context.setEncoding(reader.getEncoding());
            ImportDtoInfo dtoInfo;
            while ((dtoInfo = reader.next()) != null) {
                final boolean isTypeIgnore = domainFacadeIgnoreService.isIgnore(dtoInfo.getType(), FACADE_NAME);
                if (isTypeIgnore) {
                    log.debug(String.format("Тип %s игнорируется, поэтому исключен объект с ID %s.",
                            dtoInfo.getId(), dtoInfo.getType()));
                    continue;
                }

                final XfwClass xfwClass = xfwModel.findBySimpleName(dtoInfo.getType(), XfwClass.class);
                if (xfwClass != null) {
                    dtoInfo = filterIgnoreFields(dtoInfo);
                    if (ignoreBidirectional) {
                        dtoInfo = ImportContextUtils.createDtoInfoWithoutBidirectionalLinks(dtoInfo, xfwClass);
                    }
                    addDtoToContext(xfwClass, dtoInfo);
                    removeDataOfExcessive(dtoInfo);
                } else {
                    if (isIgnoreObjectsOfUnknownType) {
                        log.debug(String.format("При импорте был обнаружен объект ID %s неизвестного типа %s.",
                                dtoInfo.getType(),
                                dtoInfo.getId()));
                    } else {
                        throw new ImportException(String
                                .format("Объект %s имеет неизвестный тип %s", dtoInfo.getType(), dtoInfo.getId()));
                    }
                }
            }
        }
        mergeHierarchicalTypes();
        findNotResolveDependencies();
    }

    /**
     * Создаёт копию или возращает неизменный dto без полей игнорируемых аннотацией {@link XFWFacadeIgnore}.
     *
     * @param dtoInfo исходный dto
     * @return dto без полей игнорируемых аннотацией.
     *
     * @since 1.8.5
     */
    private ImportDtoInfo filterIgnoreFields(ImportDtoInfo dtoInfo) {
        final List<String> ignoredFields = domainFacadeIgnoreService
                .getIgnoredFields(dtoInfo.getType(), FACADE_NAME);
        return ignoredFields.isEmpty() ? dtoInfo : ImportContextUtils.copyWithoutFields(dtoInfo, ignoredFields);
    }


    /**
     * Формирует списки неразрешенных зависимостей(ссылки на не известные объекты) для каждого объекта.
     */
    private void findNotResolveDependencies() {
        for (Map<String, ImportDtoInfo> group : context.getObjects().values()) {
            for (ImportDtoInfo dtoInfo : group.values()) {
                for (ImportDtoInfo dependency : dtoInfo.getDependenciesOfNotResolvedInSource()) {
                    if (!findDomainObjectByTypeAndId(dependency.getType(), dependency.getId())) {
                        dtoInfo.getDependenciesOfNotResolved().add(dependency);
                    }
                }
            }
        }
    }

    /** Объединяет объекты, с одниковым ID находящиеся в одном дереве наследовнания, в один
     *  с самым расширеным типом.
     */
    private void mergeHierarchicalTypes() {
        for (String type : context.getObjects().keySet()) {
            final List<String> parentTypes = ImportContextUtils
                    .findAllSuperTypesForType(xfwModel.findBySimpleName(type, XfwClass.class).getInstanceClass());
            final Map<String, ImportDtoInfo> deletedObjects = new HashMap<>();

            for (Map.Entry<String, ImportDtoInfo> object : context.getObjects().get(type).entrySet()) {
                for (String parentType : parentTypes) {
                    final Map<String, ImportDtoInfo> parentObjects = context.getObjects().get(parentType);
                    if (parentObjects != null && parentObjects.containsKey(object.getKey())) {
                        final ImportDtoInfo dtoInfoIsDeleted = parentObjects.remove(object.getKey());
                        ImportContextUtils.replaceObjectInContext(context, dtoInfoIsDeleted, object.getValue());
                        deletedObjects.put(parentType, dtoInfoIsDeleted);
                    }
                }
            }

            // удаляем объекты, которые будут загруженны в дочеренм типе
            for (Map.Entry<String, ImportDtoInfo> entry : deletedObjects.entrySet()) {
                context.getObjects().get(entry.getKey()).remove(entry.getValue().getId());
            }
        }
    }

    /** Удаляем излишние данные из {@link ImportDtoInfo}. Для экономии памяти.
     *  @param dtoInfo объект, который будет очищен.
     */
    private void removeDataOfExcessive(ImportDtoInfo dtoInfo) {
        dtoInfo.setDomainTo(null);
    }

    /** Добавляет информацию о новом доменном объекте в контекст и обновляет контекст.
     * @param dtoInfo Информация о доменном объекте. Объект может быть преобразован.
     * @param xfwClass метамодель доменного объекта.
     * @return {@link ImportDtoInfo} который находится в контексте.
     *
     * @throws ImportException если возникла проблема при импорте.
     */
    private ImportDtoInfo addDtoToContext(XfwClass xfwClass, ImportDtoInfo dtoInfo) throws ImportException {
        final List<TypeAndFieldName> propertiesOfDependency = findNamesOfDependencyProperties(xfwClass);
        final List<ImportDtoInfo> dependencies = findAllDependencies(dtoInfo.getDomainTo(), propertiesOfDependency);
        dtoInfo = ImportContextUtils.addOrMergeObject(context, dtoInfo);
        addDependenciesToDtoInfo(dtoInfo, dependencies);
        ImportContextUtils.removeDependencyFromDependenciesOfSource(context, dtoInfo);
        return dtoInfo;
    }

    /** Добавляет в списки зависимостей {@link ImportDtoInfo} зависимсоти из списка.
     * @param dtoInfo объект в который будут добавлены зависимости.
     * @param dependencies спискок добовляемых зависимостей.
     */
    private void addDependenciesToDtoInfo(ImportDtoInfo dtoInfo, List<ImportDtoInfo> dependencies) {
        // все зависимости
        dtoInfo.getDependencies().addAll(dependencies);
        // не разрешенные зависимости источника
        for (ImportDtoInfo dependency : dependencies) {
            if (!dependency.isLoadFromFile()) {
                dtoInfo.getDependenciesOfNotResolvedInSource().add(dependency);
            }
        }
    }

    /**
     * Проверяет наличие доменного объекта врепозитории по типу и идентификатору.
     *
     *  @param type тип доменного объекта.
     *  @param id идентификатор.
     *  @return если объект в репозитории есить true, иначе false.
     */
    private boolean findDomainObjectByTypeAndId(String type, String id) {
        final DomainService domainService = resolver.resolveService(type);
        try {
            return domainService != null && domainService.getObjectById(id) != null;
        } catch (XObjectNotFoundException e) {
            // объект не нашелся
        }
        return false;
    }

    /**
     * Возвращает список имя + тип полей класса, имеющих зависимости на другие объекты.
     *
     * @param xfwClass описание класса объекта.
     * @return список имен и типов полей класса имеющих зависимости от других объектов.
     */
    private List<TypeAndFieldName> findNamesOfDependencyProperties(XfwClass xfwClass) {
        final List<TypeAndFieldName> namesOfFields = new ArrayList<>();
        for (XfwReference field : xfwClass.getEAllReferences()) {
            namesOfFields.add(new TypeAndFieldName(field.getEType().getName(), field.getName()));
        }
        return namesOfFields;
    }


    /** Формирует список зависимсотей DTO для указаного списка свойств.
     *  @param domainTo DTO для которого выполняется операция.
     *  @param properties список свойств содержащих зависимости от других объектов.
     *  @return список {link ImportDtoInfo} от которых зависит данный объект.
     */
    private List<ImportDtoInfo> findAllDependencies(DomainTo domainTo, List<TypeAndFieldName> properties) {
        final List<ImportDtoInfo> dependencies = new ArrayList<>();

        for (TypeAndFieldName property : properties) {
            final Object value = domainTo.getProperty(property.fieldName);
            if (value == null) {
                continue;
            } else if (value instanceof Collection) {
                for (String id : (Collection<String>) value) {
                    final ImportDtoInfo dtoInfo = ImportContextUtils.getOrCreateObject(context, property.type, id);
                    dependencies.add(dtoInfo);
                }
            } else {
                final String id = (String) value;
                final ImportDtoInfo dtoInfo = ImportContextUtils.getOrCreateObject(context, property.type, id);
                dependencies.add(dtoInfo);
            }
        }

        return dependencies;
    }

    /**
     * Вспомогательный класс, содержащий в себе тип и имя поля.
     */
    private static class TypeAndFieldName {
        final String type;
        final String fieldName;

        public TypeAndFieldName(String type, String fieldName) {
            this.type = type;
            this.fieldName = fieldName;
        }
    }
}
