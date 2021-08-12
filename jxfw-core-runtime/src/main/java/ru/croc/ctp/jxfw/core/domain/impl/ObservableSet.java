package ru.croc.ctp.jxfw.core.domain.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Обертка с отслеживанием изменений множества.
 *
 * @param <T> тип
 * @author Alexander Golovin
 * @since 1.6
 */
public class ObservableSet<T> implements Set<T> {
    private Set<T> set;
    private Consumer<Collection<?>> callback;

    /**
     * Обертка с отслеживанием изменений множества.
     *
     * @param set множество
     * @param callback обработчик событий изменения.
     */
    public ObservableSet(Set<T> set, Consumer<Collection<?>> callback) {
        this.set = set;
        this.callback = callback;
    }


    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(T t) {
        callback.accept(Collections.singletonList(t));
        return set.add(t);
    }

    @Override
    public boolean remove(Object o) {
        callback.accept(Collections.singletonList(o));
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        callback.accept(c);
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        callback.accept(c);
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        callback.accept(c);
        return set.removeAll(c);
    }

    @Override
    public void clear() {
        callback.accept(Collections.unmodifiableSet(set));
        set.clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return set.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        callback.accept(null);
        return set.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return set.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return set.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        set.forEach(action);
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return set.equals(obj);
    }

    @Override
    public String toString() {
        return set.toString();
    }
}
