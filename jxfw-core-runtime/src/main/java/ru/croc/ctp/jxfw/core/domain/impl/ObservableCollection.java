package ru.croc.ctp.jxfw.core.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Обертка с отслеживанием изменений коллекции.
 *
 * @param <T> тип
 * @author Alexander Golovin
 * @since 1.6
 */
public class ObservableCollection<T> implements Collection<T> {
    private Collection<T> collection;
    private Consumer<Collection<?>> callback;

    /**
     * Обертка с отслеживанием изменений коллекции.
     *
     * @param collection коллекция
     * @param callback обработчик событий изменения.
     */
    public ObservableCollection(Collection<T> collection, Consumer<Collection<?>> callback) {
        this.collection = collection;
        this.callback = callback;
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return collection.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }

    @Override
    public Object[] toArray() {
        return collection.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return collection.toArray(a);
    }

    @Override
    public boolean add(T t) {
        callback.accept(Collections.singletonList(t));
        return collection.add(t);
    }

    @Override
    public boolean remove(Object o) {
        callback.accept(Collections.singletonList(o));
        return collection.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        callback.accept(c);
        return collection.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        callback.accept(c);
        return collection.retainAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        callback.accept(c);
        return collection.retainAll(c);
    }

    @Override
    public void clear() {
        callback.accept(new ArrayList<>(collection));
        collection.clear();
    }
}
