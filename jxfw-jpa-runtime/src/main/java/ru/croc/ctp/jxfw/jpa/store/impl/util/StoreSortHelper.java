package ru.croc.ctp.jxfw.jpa.store.impl.util;

import java8.util.Lists;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInterruptedException;
import ru.croc.ctp.jxfw.jpa.store.impl.comparator.DebugComparators;
import ru.croc.ctp.jxfw.jpa.store.impl.comparator.RuntimeComparator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.OneToOne;

/**
 * Runtime-часть сортировки для правильного порядка сохранения.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
public abstract class StoreSortHelper {

    private static final Logger log = LoggerFactory.getLogger(StoreSortHelper.class);

    private static final RuntimeComparator RUNTIME_COMPARATOR = new RuntimeComparator();

    /**
     * Проверка, является ли объект o2 значением скалярного свойства объекта o1.
     * @param o1 - доменный объект o1
     * @param o2 - доменный объект o2
     * @return результат проверки
     */
    private static boolean isScalarFieldOf(final DomainObject<?> o1, final DomainObject<?> o2) {
        for (Field f: getFieldsUpTo(o1.getClass(), Object.class)) {     
            f.setAccessible(true);
            OneToOne scalar = f.getAnnotation(OneToOne.class);
            if(!Objects.isNull(scalar) && !"".equals(scalar.mappedBy())){
                //подразумевается что раз тут связь один к одному, child должен быть тот у которого заполнен mappedBy
                continue;
            }
            try {
                if (f.get(o1) != null && f.get(o1).equals(o2)) {
                    return true;
                }
            } catch (org.hibernate.LazyInitializationException lie) {
                //collection not initialized
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(
                        "Error when compare objects:" + o1 + ", " + o2, e);
            }
        }
        return false;
    }
    
    private static Iterable<Field> getFieldsUpTo(@Nonnull Class<?> startClass, @Nullable Class<?> exclusiveParent) {
        List<Field> currentClassFields = com.google.common.collect.Lists.newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

    private static void sort(List<? extends DomainObject<?>> list) throws XInterruptedException {
        Map<DomainObject<?>, String> objects = new HashMap<>();
        Map<DomainObject<?>, Node<DomainObject<?>>> trees = new HashMap<>();
        for (DomainObject<?> o1 : list) {
            checkInterrupt();
            for (DomainObject<?> o2 : list) {
                if (isScalarFieldOf(o1, o2)) {
                    addToTrees(trees, o1, o2);
                }
            }
        }

        {
            int idx = 1;
            for (Object domainObject : trees.keySet()) {
                checkInterrupt();
                Node<DomainObject<?>> node = trees.get(domainObject);
                String sortingIndex = StringUtils.repeat("B", idx);
                putChilds(sortingIndex, node, objects);
                idx++;
            }
        }

        Comparator<DomainObject<?>> comparator = createComparator(objects);

        if (log.isDebugEnabled()) {
            DebugComparators.verifyTransitivity(comparator, list);
        }

        Lists.sort(list, comparator);
    }
    
    
    private static Comparator<DomainObject<?>> createComparator(Map<? super DomainObject<?>, String> objects) {
        return new Comparator<DomainObject<?>>() {
            Map<DomainObject<?>, Map<DomainObject<?>, Integer>> comparedObjects = new HashMap<>();

            @Override
            public int compare(DomainObject<?> o1, DomainObject<?> o2) {
                if (o1.getId().equals(o2.getId()) && o1.getTypeName().equals(o2.getTypeName())) {
                    return 0;
                }

                Optional<Integer> res = Optional
                        .ofNullable(((comparedObjects.containsKey(o1) && comparedObjects.get(o1).containsKey(o2))
                                ? comparedObjects.get(o1).get(o2)
                                : ((comparedObjects.containsKey(o2) && comparedObjects.get(o2).containsKey(o1))
                                        ? comparedObjects.get(o2).get(o1) * (-1)
                                        : null)));

                if (res.isPresent()) {
                    return res.get();
                }

                String objectsStrO1 = Optional.ofNullable(objects.get(o1)).orElse("");
                String objectsStrO2 = Optional.ofNullable(objects.get(o2)).orElse("");

                int value = (-1) * objectsStrO1.compareTo(objectsStrO2);
                if (value == 0) {
                    value = RUNTIME_COMPARATOR.compare(o1, o2);
                }

                if (value == 0) {
                    value = o1.getTypeName().compareTo(o2.getTypeName());
                }

                if (comparedObjects.get(o1) == null) {
                    comparedObjects.put(o1, new HashMap<>());
                }
                comparedObjects.get(o1).put(o2, value);

                return value;
            }
        };
    }

    private static void putChilds(String prefix, Node<DomainObject<?>> node,
            Map<? super DomainObject<?>, String> objects) throws XInterruptedException {
        Set<Node<DomainObject<?>>> used = new HashSet<>();
        Queue<Node<DomainObject<?>>> queue = new LinkedList<>();
        queue.add(node);
        used.add(node);

        int idx = 1;
        while (!queue.isEmpty()) {
            checkInterrupt();
            node = queue.poll();

            String sortingIndex = prefix + "A" + StringUtils.repeat("B", idx);
            log.debug("Sorting index: {}, data: {}", sortingIndex, node.getData());
            objects.put(node.getData(), sortingIndex);

            idx++;
            for (Node<DomainObject<?>> n : node.getChildren()) {
                if (used.contains(n)) {
                    continue;
                }
                queue.add(n);
                used.add(n);
            }
        }
    } 

    private static void addToTrees(Map<? super DomainObject<?>, Node<DomainObject<?>>> trees, 
            DomainObject<?> parentObject,
            DomainObject<?> childObject) {
                
        Node<DomainObject<?>> child = trees.remove(childObject);
        if (child == null) {
            child = findInTrees(trees, childObject);
            if (child == null) {
                child = new Node<>(childObject);
            } else {
                if (!child.getChildren().isEmpty()) {
                    for (Node<DomainObject<?>> node : child.getChildren()) {
                        if (node.getChildren().contains(child)) {
                            child = new Node<>(childObject);
                            break;
                        }
                    }
                }
            }
        }
        Node<DomainObject<?>> parent = findInTrees(trees, parentObject);
        if (parent == null) {
            parent = new Node<>(parentObject);
            trees.put(parentObject, parent);
        }
        if (!child.getChildren().contains(parent)) {
            parent.addChild(child);
        }
    }
    
    private static Node<DomainObject<?>> findInTrees(Map<? super DomainObject<?>, Node<DomainObject<?>>> trees, 
            DomainObject<?> objectToFind) {
        Node<DomainObject<?>> result = trees.get(objectToFind);
        if (result == null) {
            Collection<Node<DomainObject<?>>> nodes = trees.values();
            Set<Node<DomainObject<?>>> knownNodes = new HashSet<>();
            result = findInNodes(nodes, objectToFind, knownNodes);
        }
        return result;
    }

    private static Node<DomainObject<?>> findInNodes(Collection<Node<DomainObject<?>>> nodes,
            DomainObject<?> objectToFind, Set<Node<DomainObject<?>>> knownNodes) {
        Node<DomainObject<?>> result = null;
        for (Node<DomainObject<?>> n : nodes) {
            if (knownNodes.contains(n)) {
                continue;
            }
            if (n.getData().equals(objectToFind)) {
                result = n;
                break;
            } else {
                knownNodes.add(n);
                result = findInNodes(n.getChildren(), objectToFind, knownNodes);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Упорядочивание элементов для сохранения на основе данных которые были получены
     * на стадии компиляции/кодогенерации и в рантайме.
     *
     * @param input - входной список элементов
     * @throws XInterruptedException если на текущем {@link Thread} был вызван метод interrupt.
     */
    @SuppressWarnings("unchecked")
    public static void sortWithCompileAndRuntimeInfo(final List<? extends DomainObject<?>> input)
            throws XInterruptedException {
        sort(input);
        
        List<? extends DomainObject<?>> newObjects = getNewObjects(input);
        List<? extends DomainObject<?>> editedObjects = getEditedObjects(input);
        List<? extends DomainObject<?>> removedObjects = getRemovedObjects(input);
        input.clear();
        ((List<? super DomainObject<?>>)input).addAll(newObjects);
        ((List<? super DomainObject<?>>)input).addAll(editedObjects);
        Collections.reverse(removedObjects);
        ((List<? super DomainObject<?>>)input).addAll(removedObjects);
    }
    
    /**
     * Узел дерева доменных объектов.
     * @author AKogun
     *
     * @param <T> тип доменного объекта
     */
    private static class Node<T extends DomainObject<?>> {
        
        private List<Node<T>> children = new ArrayList<>();
        private T data = null;

        /**
         * Конструктор.
         * @param data доменный объект
         */
        private Node(T data) {
            this.data = data;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void addChild(Node<T> child) {
            this.children.add(child);
        }

        public T getData() {
            return this.data;
        }
        
        @Override
        public String toString() {
            return "[data: " + data.toString() + ", children: " + children + "]";
        }
    }

    private static List<? extends DomainObject<?>> getNewObjects(List<? extends DomainObject<?>> uow) {
        return StreamSupport.stream(uow).filter((domainObject) -> domainObject.isNew()).collect(Collectors.toList());
    }

    private static List<? extends DomainObject<?>> getEditedObjects(List<? extends DomainObject<?>> uow) {
        return StreamSupport.stream(uow).filter((domainObject) -> !domainObject.isNew() && !domainObject.isRemoved()).collect(Collectors.toList());
    }
    
    private static List<? extends DomainObject<?>> getRemovedObjects(List<? extends DomainObject<?>> uow) {
        return StreamSupport.stream(uow).filter((domainObject) -> domainObject.isRemoved()).collect(Collectors.toList());
    }

    /**
     * Проверяет был ли поток прерван и если да, то выбрасывает исключение.
     * @throws XInterruptedException если на текущем {@link Thread} был вызван метод interrupt.
     */
    private static void checkInterrupt() throws XInterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new XInterruptedException("Store sorting is interrupted!");
        }
    }
}