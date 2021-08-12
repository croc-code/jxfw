package ru.croc.ctp.jxfw.core.domain.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Обертка с отслеживанием изменений списка.
 *
 * @param <T> тип
 * @author Alexander Golovin
 * @since 1.6
 */
public class ObservableList<T> implements List<T> {
    private List<T> list;
    private Consumer<Collection<?>> callback;

    /**
     * Обертка с отслеживанием изменений списка.
     *
     * @param list список
     * @param callback обработчик событий изменения.
     */
    public ObservableList(List<T> list, Consumer<Collection<?>> callback) {
        this.list = list;
        this.callback = callback;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        callback.accept(Collections.singletonList(t));
        return list.add(t);
    }

    @Override
    public boolean remove(Object o) {
        callback.accept(Collections.singletonList(o));
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        callback.accept(c);
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        callback.accept(c);
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        callback.accept(c);
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        callback.accept(c);
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        callback.accept(Collections.unmodifiableList(list));
        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        callback.accept(Collections.singletonList(element));
        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        callback.accept(Collections.singletonList(element));
        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        callback.accept(Collections.singletonList(list.get(index)));
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        callback.accept(null);
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        callback.accept(null);
        list.sort(c);
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        callback.accept(null);
        return list.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return list.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return list.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        list.forEach(action);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return list.equals(obj);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
