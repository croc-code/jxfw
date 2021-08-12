package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToMany;
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToOne;
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToMany;
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToOne;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Вспомогательные функции для работы с {@link DefaultImportContext}.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class ImportContextUtils {

    /** Находит или создаёт новый {@link ImportDtoInfo} по типу и идетификатору.
     * Если создаётся новый объект, то он не содержит в себе DTO.
     * @param context контест импорта.
     * @param type тип доменного объекта.
     * @param id идентификатор.
     * @return {@link ImportDtoInfo} по типу и ИД.
     */
    public static ImportDtoInfo getOrCreateObject(DefaultImportContext context, String type, String id) {
        Map<String, ImportDtoInfo> objectsOfType = context.getObjects().get(type);
        if (objectsOfType == null) {
            objectsOfType = new HashMap<>();
            context.getObjects().put(type, objectsOfType);
        }

        ImportDtoInfo dtoInfo = objectsOfType.get(id);
        if (dtoInfo == null) {
            dtoInfo = new ImportDtoInfo(type, id);
            objectsOfType.put(id, dtoInfo);
        }

        return dtoInfo;
    }

    /** Находит {@link ImportDtoInfo} по типу и идетификатору.
     * @param context контест импорта.
     * @param type тип доменного объекта.
     * @param id идентификатор.
     * @return {@link ImportDtoInfo} по типу и ИД, если не найден вернется null.
     */
    public static ImportDtoInfo findObject(DefaultImportContext context, String type, String id) {
        final Map<String, ImportDtoInfo> objectsOfType = context.getObjects().get(type);
        if (objectsOfType == null) {
            return null;
        }
        return objectsOfType.get(id);
    }

    /** Объединяет передавемый {@link ImportDtoInfo} с уже частично созданным или добавляет новый в контексте.
     * @param context контест импорта.
     * @param dtoInfo добовляемый объект.
     * @return ссылка на объект, который лежит в контексте.
     */
    public static ImportDtoInfo addOrMergeObject(DefaultImportContext context, ImportDtoInfo dtoInfo) {
        Map<String, ImportDtoInfo> objectsOfType = context.getObjects().get(dtoInfo.getType());
        if (objectsOfType == null) {
            objectsOfType = new HashMap<>();
            context.getObjects().put(dtoInfo.getType(), objectsOfType);
        }

        final ImportDtoInfo actualDtoInfo = objectsOfType.get(dtoInfo.getId());
        if (actualDtoInfo == null) {
            objectsOfType.put(dtoInfo.getId(), dtoInfo);
            return dtoInfo;
        }

        merge(dtoInfo, actualDtoInfo);
        return actualDtoInfo;
    }

    /** Переносит данные из from в to.
     * @param from источник данных
     * @param to получатель данных
     */
    private static void merge(ImportDtoInfo from, ImportDtoInfo to) {
        to.setDomainTo(from.getDomainTo());
        to.setOffsetFirstByteInFile(from.getOffsetFirstByteInFile());
        to.setOffsetLastByteInFile(from.getOffsetLastByteInFile());
    }

    /** Удаляет из списков зависимостей источника переданный объект.
     * @param context контест импорта.
     * @param dtoInfoOfRemoving объект зависимоти от которого будут удалены.
     */
    public static void removeDependencyFromDependenciesOfSource(DefaultImportContext context,
                                                                ImportDtoInfo dtoInfoOfRemoving) {
        for (Map<String, ImportDtoInfo> dtoInfoOfType : context.getObjects().values()) {
            for (ImportDtoInfo dtoInfo : dtoInfoOfType.values()) {
                dtoInfo.getDependenciesOfNotResolvedInSource().remove(dtoInfoOfRemoving);
                dtoInfo.getDependenciesOfNotResolved().remove(dtoInfoOfRemoving);
            }
        }
    }

    /** Очищает содержимое контекста.
     * @param context контекст которые будет очищен.
     */
    public static void clear(DefaultImportContext context) {
        context.setObjects(new HashMap<>());
        context.setGroupsOfLoading(new HashMap<>());
    }

    /** Подготавливает контекст к началу загрузки данных.
     * @param context контекст которые будет очищен.
     */
    public static void initProgressLoading(DefaultImportContext context) {
        // в дальнейшей загрузке не участвует
        context.setObjects(null);
    }

    /** Заменяет один объект в контексте на другой.
     * @param context контекст импорта.
     * @param source заменяемый объект.
     * @param target новый объект.
     */
    public static void replaceObjectInContext(DefaultImportContext context, ImportDtoInfo source,
                                              ImportDtoInfo target) {
        for (Map<String, ImportDtoInfo> dtoInfoOfType : context.getObjects().values()) {
            for (ImportDtoInfo dtoInfo : dtoInfoOfType.values()) {
                if (dtoInfo.getDependencies().remove(source)) {
                    dtoInfo.getDependencies().add(target);
                }
                if (dtoInfo.getDependenciesOfNotResolvedInSource().remove(source) && !target.isLoadFromFile()) {
                    dtoInfo.getDependenciesOfNotResolvedInSource().add(target);
                }
                if (dtoInfo.getDependenciesOfNotResolved().remove(source) && !target.isLoadFromFile()) {
                    dtoInfo.getDependenciesOfNotResolved().add(target);
                }
            }
        }
    }

    /**
     * Возвращает цепочку типов, расширенных переданным типом.
     *
     * @param clazz тип.
     * @return цепочка базовых типов.
     */
    public static List<String> findAllSuperTypesForType(Class<?> clazz) {
        final List<String> superTypes = new ArrayList<>();

        Class parent = clazz.getSuperclass();
        while (parent != null && !parent.getName().equals(Object.class.getName())) {
            superTypes.add(parent.getSimpleName());
            parent = parent.getSuperclass();
        }

        return superTypes;
    }

    /**
     * Удаляет из DTO дублирующие двухнаправленные ссылки.
     *
     * @param dtoInfo dto объект
     * @param xfwClass описание класса объекта.
     */
    public static ImportDtoInfo createDtoInfoWithoutBidirectionalLinks(ImportDtoInfo dtoInfo, XfwClass xfwClass) {
        final List<String> ignoreFields = new ArrayList<>();
        for (XfwReference field : xfwClass.getEAllReferences()) {
            if (isMappedBy(field)) {
                ignoreFields.add(field.getName());
            }
        }

        final DomainTo newDomainTo = new DomainTo();
        {//копируем DTO, исключая поля из списка
            newDomainTo.setType(dtoInfo.getType());
            newDomainTo.setId(dtoInfo.getId());
            newDomainTo.setNew(dtoInfo.getDomainTo().isNew());
            newDomainTo.setRemoved(dtoInfo.getDomainTo().isRemoved());
            newDomainTo.setTs(dtoInfo.getDomainTo().getTs());
            dtoInfo.getDomainTo().forEachProperty((property, value) -> {
                if (!ignoreFields.contains(property)) {
                    newDomainTo.addProperty(property, value);
                }
            });
            dtoInfo.getDomainTo().getPropertyMetadata().forEach((property, value) -> {
                if (!ignoreFields.contains(property)) {
                    newDomainTo.addPropertyMetadata(property, value);
                }
            });
        }

        return new ImportDtoInfo.Builder(dtoInfo.getType(), dtoInfo.getId())
                .domainTo(newDomainTo)
                .offsetFirstByteInFile(dtoInfo.getOffsetFirstByteInFile())
                .offsetLastByteInFile(dtoInfo.getOffsetLastByteInFile())
                .build();
    }

    /**
     * Создаёт копию переданного dtoInfo, без переданных свойств и их мета-информации.
     *
     * @param dtoInfo исходный {@link ImportDtoInfo}
     * @param fieldsNames список исключаемых полей
     * @return новый dtoInfo без указанных полей.
     */
    public static ImportDtoInfo copyWithoutFields(ImportDtoInfo dtoInfo, List<String> fieldsNames) {
        return new ImportDtoInfo.Builder(dtoInfo.getType(), dtoInfo.getId())
                .offsetFirstByteInFile(dtoInfo.getOffsetFirstByteInFile())
                .offsetLastByteInFile(dtoInfo.getOffsetLastByteInFile())
                .domainTo(copyWithoutFields(dtoInfo.getDomainTo(), fieldsNames))
                .build();
    }

    /**
     * Создаёт копию переданного dto, без переданных свойств и их мета-информации.
     *
     * @param dto исходный {@link DomainTo}
     * @param fieldsNames список исключаемых полей
     * @return новый dto без указанных полей.
     */
    protected static DomainTo copyWithoutFields(DomainTo dto, List<String> fieldsNames) {
        final DomainTo result = new DomainTo(dto.getType(), dto.getId());
        dto.forEachProperty((property, value) -> {
            if (!fieldsNames.contains(property)) {
                result.addProperty(property, value);
            }
        });
        dto.getPropertyMetadata().forEach((property, value) -> {
            if (!fieldsNames.contains(property)) {
                result.addPropertyMetadata(property, value);
            }
        });
        return result;
    }

    /**
     * Проверяет является ли переданное поле отображением основного поля.
     *
     * @param field описание поля
     * @return true, если является отображением.
     */
    private static boolean isMappedBy(XfwReference field) {
        final XfwAnnotation annotation = getNavigableAnnotation(field);
        return annotation != null && annotation.getDetails().containsKey("mappedBy")
                && !annotation.getDetails().get("mappedBy").isEmpty();
    }

    /**
     * Аннотации навигируемых свойств.
     */
    private static final Class<?>[] navigableAnnotations = new Class[] {
            XFWOneToOne.class, XFWOneToMany.class, XFWManyToOne.class, XFWManyToMany.class
    };

    /**
     * Возвращает описание навигируемой аннотации поля.
     *
     * @param field описание поля
     * @return описание навигируемой аннотации или null.
     */
    private static XfwAnnotation getNavigableAnnotation(XfwReference field) {
        for (Class<?> clazz : navigableAnnotations) {
            final XfwAnnotation annotation = field.getEAnnotation(XFWConstants.getUri(clazz.getSimpleName()));
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
}
