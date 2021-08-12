package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContext;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Упорядочивает и распределяет объекты по упорядоченным группам заданного размера.
 *
 * @author Golovin Alexander
 * @since 1.6
 */
@Component("importContextSplitterAndAggregator")
public class ImportSequenceGroupContextSplitterAndAggregator implements ImportContextSplitterAndAggregator {
    /** Желаемый размер групп объектов. */
    private int packageSize = 100;

    @Override
    public void split(ImportContext importContext) {
        final DefaultImportContext context = castImportContext(importContext);
        final Map<Long, Set<ImportDtoInfo>> groups = topologicalSort(context);
        context.getGroupsOfLoading().putAll(groups);
        ImportContextUtils.initProgressLoading(context);
    }

    @Override
    public void aggregate(ImportContext importContext) {
        final DefaultImportContext context = castImportContext(importContext);
        final Map<Long, Set<ImportDtoInfo>> groups = importContext.getGroupsOfLoading();
        context.setGroupsOfLoading(new HashMap<>());

        packGroupsToContext(context, groups);
    }

    /**
     * Проверяет и приводит контекст к известному типу.
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
            if (currentPackageSize + group.getValue().size() <= packageSize) {
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

    /**
     * Топологическая сортировка графа объектов на группы минимального размера ссылающиеся только
     * на группы с меньшим номером.
     *
     * @param context контекст
     * @return группы объектов.
     */
    private Map<Long, Set<ImportDtoInfo>> topologicalSort(DefaultImportContext context) {
        // объекты для импорта и их зависимости, которые еще не добавлены в группы
        final Map<ImportDtoInfo, Set<ImportDtoInfo>> graph = createGraph(context);
        final Map<Long, Set<ImportDtoInfo>> groups = new HashMap<>(); // формируемая последовательность групп
        long index = 0; // индекс для следующей группы

        while (!graph.isEmpty()) {
            final Set<ImportDtoInfo> leaves = findLeavesInGraph(graph);
            for (ImportDtoInfo dtoInfo : leaves.isEmpty() ? new HashSet<>(graph.keySet()) : leaves) {
                final Deque<ImportDtoInfo> queue = new LinkedList<>(); // очередь для обхода графа в глубину
                if (!graph.containsKey(dtoInfo)) {
                    continue;
                }
                queue.addLast(dtoInfo);

                final Map<ImportDtoInfo, Integer> visits = new HashMap<>(); // кол-во посищений узлов
                int timeOfLastChange = 0; // счётчик итераций с момента последнего добавления объекта в группу

                while (!queue.isEmpty()) {
                    final ImportDtoInfo node = queue.element();
                    timeOfLastChange++;
                    visit(visits, node);

                    boolean ready = true; // все ли зависимости добавлены в группы
                    for (ImportDtoInfo dependency : node.getDependencies()) {
                        if (!graph.containsKey(dependency)) {
                            continue;
                        }

                        if (!isVisited(visits, dependency)) { // если не посещали, то в начало очереди
                            queue.remove(dependency);
                            queue.addFirst(dependency);
                        }
                        ready = false;
                    }

                    if (ready) { // добавляем группу без циклов
                        addGroup(groups, index++, Collections.singleton(node));
                        queue.removeFirst();
                        visits.remove(node);
                        removeNodeFromGraph(graph, node);

                        timeOfLastChange = 0;
                    } else if (timeOfLastChange > 2 * queue.size()) { // отлавливаем циклы
                        if (isAllDependenciesResolved(graph, queue)) {
                            addGroup(groups, index++, queue);
                            removeNodesFromGraph(graph, queue);
                            visits.clear();
                            queue.clear();
                        }

                        timeOfLastChange = 0;
                    } else if (queue.element().equals(node)) { // прокручиваем очередь
                        queue.remove(node);
                        queue.addLast(node);
                    }
                }
            }
        }
        return groups;
    }

    /**
     * Создаёт граф по контексту.
     *
     * @param context контекст
     * @return граф.
     */
    private Map<ImportDtoInfo, Set<ImportDtoInfo>> createGraph(DefaultImportContext context) {
        final Map<ImportDtoInfo, Set<ImportDtoInfo>> graph = new HashMap<>();
        for (Map<String, ImportDtoInfo> objectsOfType: context.getObjects().values()) {
            for (ImportDtoInfo object: objectsOfType.values()) {
                graph.put(object, new HashSet<>(object.getDependencies()));
            }
        }
        return graph;
    }

    /**
     * Добавляет группу объектов.
     *
     * @param groups группы
     * @param index индекс группы
     * @param objects объекты.
     */
    private void addGroup(Map<Long, Set<ImportDtoInfo>> groups, long index, Collection<ImportDtoInfo> objects) {
        final Set<ImportDtoInfo> group = new HashSet<>();
        for (ImportDtoInfo object: objects) {
            if (object.isLoadFromFile()) {
                group.add(object);
            }
        }
        groups.put(index, group);
    }

    /**
     * Проверяет все ли зависимоти в очереди или группах.
     *
     * @param graph граф
     * @param queue очередь
     * @return false, если хотя бы одна зависимоть не разрешена.
     */
    private boolean isAllDependenciesResolved(Map<ImportDtoInfo, Set<ImportDtoInfo>> graph,
                                              Deque<ImportDtoInfo> queue) {
        for (ImportDtoInfo node: queue) {
            for (ImportDtoInfo dependency: node.getDependencies()) {
                if (graph.containsKey(dependency) && !queue.contains(dependency)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Находит листья(объекты без зависимостей) в графе.
     *
     * @param graph граф.
     * @return листья.
     */
    private Set<ImportDtoInfo> findLeavesInGraph(Map<ImportDtoInfo, Set<ImportDtoInfo>> graph) {
        final Set<ImportDtoInfo> leaves = new HashSet<>();
        for (Map.Entry<ImportDtoInfo, Set<ImportDtoInfo>> node: graph.entrySet()) {
            if (node.getValue().isEmpty()) {
                ImportDtoInfo key = node.getKey();
                leaves.add(key);
            }
        }
        return leaves;
    }

    /**
     * Отмечает посещение узла.
     *
     * @param visits счётчики посещений
     * @param node узел
     */
    private void visit(Map<ImportDtoInfo, Integer> visits, ImportDtoInfo node) {
        if (!visits.containsKey(node)) {
            visits.put(node, 1);
        } else {
            visits.put(node, visits.get(node) + 1);
        }
    }

    /**
     * Проверяет был ли узел посещен.
     *
     * @param visits счётчики посещений
     * @param node узел
     */
    private boolean isVisited(Map<ImportDtoInfo, Integer> visits, ImportDtoInfo node) {
        return visits.containsKey(node);
    }

    /**
     * Удаляет узел из графа.
     *
     * @param graph граф
     * @param node узел.
     */
    private void removeNodeFromGraph(Map<ImportDtoInfo, Set<ImportDtoInfo>> graph, ImportDtoInfo node) {
        graph.remove(node);
        for (Set<ImportDtoInfo> dependencies: graph.values()) {
            dependencies.remove(node);
        }
    }

    /**
     * Удаляет узлы из графа.
     *
     * @param graph граф
     * @param nodes узлы.
     */
    private void removeNodesFromGraph(Map<ImportDtoInfo, Set<ImportDtoInfo>> graph, Collection<ImportDtoInfo> nodes) {
        for (ImportDtoInfo node: nodes) {
            graph.remove(node);
        }
        for (Set<ImportDtoInfo> dependencies: graph.values()) {
            dependencies.removeAll(nodes);
        }
    }

    /**
     * Желаемый размер групп объектов.
     * @param maxGroupPackageSize размер группы.
     */
    public void setMaxGroupPackageSize(int maxGroupPackageSize) {
        this.packageSize = maxGroupPackageSize;
    }

    /**
     * Желаемый размер групп.
     * @return размер групп.
     */
    public int getMaxGroupPackageSize() {
        return packageSize;
    }
}