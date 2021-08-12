package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContext;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Разбивает контекст импорта на независимые друг от друга множества.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component
public class SimpleImportContextSplitterAndAggregator implements ImportContextSplitterAndAggregator {
    /** Максимальный размер группы объектов, при объединении групп.
     * Note: если группа зависимых объктов изначально больше этого значения, то она не разбивается. */
    private int maxGroupPackageSize = 100;

    @Override
    public void split(ImportContext importContext) {
        final DefaultImportContext context = castImportContext(importContext);
        final Map<Long, Set<ImportDtoInfo>> groups = splitByIndependenceGroups(context);

        // делаем ключи последовательностью чисел от 0..n
        long count = 0;
        for (Set<ImportDtoInfo> group : groups.values()) {
            context.getGroupsOfLoading().put(count++, group);
        }

        ImportContextUtils.initProgressLoading(context);
    }

    @Override
    public void aggregate(ImportContext importContext) {
        final DefaultImportContext context = castImportContext(importContext);
        final Map<Long, Set<ImportDtoInfo>> groups = importContext.getGroupsOfLoading();
        context.setGroupsOfLoading(new HashMap<>());

        packGroupsToContext(context, groups);
    }

    /** Проверяет и приводит контекст к известному типу.
     * @param importContext приводимый контекст.
     * @return привиденный контекст.
     * @throws IllegalArgumentException если тип контекста не поддерживается.
     */
    private DefaultImportContext castImportContext(ImportContext importContext) throws IllegalArgumentException {
        if (!(importContext instanceof DefaultImportContext)) {
            throw new IllegalArgumentException("Не поддерживаемый тип контекста!");
        }
        return (DefaultImportContext) importContext;
    }

    /** Разбивает объекты контекста импорта на независмые друг от друга группы.
     * @param context контекст импорта.
     * @return множество групп объектов.
     */
    private Map<Long, Set<ImportDtoInfo>> splitByIndependenceGroups(DefaultImportContext context) {
        final Map<Long, Set<ImportDtoInfo>> groups = new HashMap<>();

        long count = 0;
        for (Map<String, ImportDtoInfo> objectsOfType : context.getObjects().values()) {
            for (ImportDtoInfo dtoInfo : objectsOfType.values()) {
                if (!dtoInfo.isLoadFromFile()) {
                    continue;
                }
                final List<Long> groupsOfDependency = findLinkedGroups(dtoInfo, groups);
                // добавляем элемент и объединяем группы, если требуется
                if (groupsOfDependency.isEmpty()) {
                    createNewGroupAndPutObject(groups, dtoInfo, count++);
                    continue;
                }

                putObjectToFirstGroup(groups, dtoInfo, groupsOfDependency);
                if (groupsOfDependency.size() > 1) {
                    mergeGroupAndReplace(groups, groupsOfDependency, count++);
                }
            }
        }

        return groups;
    }

    /** Объекдиняет маленькие группы в большие и ложит их в контекст импорта.
     * @param context контекст импорта.
     * @param groups группы для упаковки.
     */
    private void packGroupsToContext(DefaultImportContext context, Map<Long, Set<ImportDtoInfo>> groups) {
        // делаем ключи последовательностью чисел от 0..n
        long count = 0;
        int currentPackageSize = 0;
        List<Long> currentGroupPackage = new ArrayList<>();

        for (Map.Entry<Long, Set<ImportDtoInfo>> group : groups.entrySet()) {
            if (currentPackageSize + group.getValue().size() <= maxGroupPackageSize) {
                // дополняем группу
                currentGroupPackage.add(group.getKey());
                currentPackageSize += group.getValue().size();
            } else {
                // переносим группу в контекст
                final Set<ImportDtoInfo> importGroup = new HashSet<>(currentPackageSize);
                for (Long keyGroup : currentGroupPackage) {
                    importGroup.addAll(groups.get(keyGroup));
                }
                context.getGroupsOfLoading().put(count++, importGroup);

                // формируем новую группу с текущим элементом
                currentGroupPackage = new ArrayList<>();
                currentGroupPackage.add(group.getKey());
                currentPackageSize = currentGroupPackage.size();
            }
        }

        // переносим последнюю группу в контекст
        if (currentPackageSize > 0) {
            final Set<ImportDtoInfo> importGroup = new HashSet<>(currentPackageSize);
            for (Long keyGroup : currentGroupPackage) {
                importGroup.addAll(groups.get(keyGroup));
                groups.remove(keyGroup);
            }
            context.getGroupsOfLoading().put(count++, importGroup);
        }
    }

    private void createNewGroupAndPutObject(Map<Long, Set<ImportDtoInfo>> groups,
                                            ImportDtoInfo dtoInfo, Long numberNewGroup) {
        final Set<ImportDtoInfo> newGroup = new HashSet<>();
        newGroup.add(dtoInfo);

        groups.put(Long.valueOf(numberNewGroup), newGroup);
    }

    private void putObjectToFirstGroup(Map<Long, Set<ImportDtoInfo>> groups,
                                       ImportDtoInfo dtoInfo, List<Long> groupsOfDependency) {
        final Long keyOfGroup = groupsOfDependency.get(0);
        groups.get(keyOfGroup).add(dtoInfo);
    }

    /** Объединяет группы в одну новую и добавляет ее с нужным идентификатором на новую.
     * @param groups все группы
     * @param groupsOfDependency список ключей групп, которые будут объединнины в одну.
     * @param numberNewGroup ключ новой группы.
     */
    private void mergeGroupAndReplace(Map<Long, Set<ImportDtoInfo>> groups, List<Long> groupsOfDependency,
                                      Long numberNewGroup) {
        final Set<ImportDtoInfo> newGroup = new HashSet<>();
        for (Long keyOfGroup : groupsOfDependency) {
            newGroup.addAll(groups.get(keyOfGroup));
            groups.remove(keyOfGroup);
        }
        groups.put(numberNewGroup, newGroup);
    }

    /** Находит группы связанные с данным объектом.
     * @param dtoInfo объект с которым ищутся связи.
     * @param groups группы объектов в которых могут быть связанные объекты.
     * @return Список ключей групп которые связанн с данным объектом.
     */
    private List<Long> findLinkedGroups(ImportDtoInfo dtoInfo, Map<Long, Set<ImportDtoInfo>> groups) {
        final List<Long> result = new ArrayList<>();

        for (Map.Entry<Long, Set<ImportDtoInfo>> group : groups.entrySet()) {
            for (ImportDtoInfo currentDtoInfo : group.getValue()) {
                if (currentDtoInfo.getDependencies().contains(dtoInfo)
                        || dtoInfo.getDependencies().contains(currentDtoInfo)) {
                    result.add(group.getKey());
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Желаемый размер групп объектов.
     *
     * @param maxGroupPackageSize размер группы.
     */
    public void setMaxGroupPackageSize(int maxGroupPackageSize) {
        this.maxGroupPackageSize = maxGroupPackageSize;
    }

    /**
     * Желаемый размер групп.
     *
     * @return размер групп.
     */
    public int getMaxGroupPackageSize() {
        return maxGroupPackageSize;
    }
}
