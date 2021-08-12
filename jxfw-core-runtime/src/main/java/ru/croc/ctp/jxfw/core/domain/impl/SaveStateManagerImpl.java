package ru.croc.ctp.jxfw.core.domain.impl;

import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.SaveStateManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Стандартная реализация {@link SaveStateManager}.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Service("saveStateManager")
public class SaveStateManagerImpl implements SaveStateManager {
    /**
     * Контекст перекрытия значений параметра.
     */
    private static class SaveStateContext {
        /**
         * Значения для классов.
         */
        Map<Class<?>, Boolean> classValues = new HashMap<>();
        /**
         * Значения для пакетов.
         */
        Map<Package, Boolean> packageValues = new HashMap<>();
        /**
         * Значение для всех классов.
         */
        Boolean enableAll = null;
        /**
         * Классы для которых выполнился сброс, но перекрытие для пакета существует.
         * Временные исключения.
         */
        Set<Class<?>> resetClasses = new HashSet<>();
        /**
         * Классы для которых выполнился сброс, но перекрытие для пакета существует.
         * Временные исключения.
         */
        Set<Package> resetPackages = new HashSet<>();
    }

    /**
     * Контекст перекрытия парматра для {@link Thread}.
     */
    private ThreadLocal<SaveStateContext> context = new ThreadLocal<>();


    @Override
    public void enable(Class<?> clazz, Class<?>... classes) {
        enable(toList(clazz, classes));
    }

    @Override
    public void enable(Collection<Class<?>> classes) {
        init();
        classes.forEach(clazz -> context.get().classValues.put(clazz, true));
        clearResetExcludes(classes);
    }

    @Override
    public void enable(Package head, Package... tail) {
        init();
        toList(head, tail).forEach(pack -> {
            context.get().packageValues.put(pack, true);
            removeClassesValuesFrom(pack);
        });
    }
    @Override
    public void enableAll() {
        resetAll();
        context.get().enableAll = true;
    }

    @Override
    public void disable(Class<?> clazz, Class<?>... classes) {
        disable(toList(clazz, classes));
    }

    @Override
    public void disable(Collection<Class<?>> classes) {
        init();
        classes.forEach(clazz -> context.get().classValues.put(clazz, false));
        clearResetExcludes(classes);
    }

    @Override
    public void disable(Package head, Package... tail) {
        init();
        toList(head, tail).forEach(pack -> {
            context.get().packageValues.put(pack, false);
            removeClassesValuesFrom(pack);
            clearResetExcludes(pack);
        });
    }

    @Override
    public void disableAll() {
        resetAll();
        context.get().enableAll = false;
    }

    @Override
    public void reset(Class<?> clazz, Class<?>... classes) {
        reset(toList(clazz, classes));
    }

    @Override
    public void reset(Collection<Class<?>> classes) {
        init();
        classes.forEach(clazz -> {
            context.get().classValues.remove(clazz);
            if (context.get().enableAll != null || context.get().packageValues.containsKey(clazz.getPackage())) {
                context.get().resetClasses.add(clazz);
            }
        });
    }

    @Override
    public void reset(Package head, Package... tail) {
        init();
        final SaveStateContext context = this.context.get();

        toList(head, tail).forEach(pack -> {
            context.packageValues.remove(pack);
            removeClassesValuesFrom(pack);
            if (context.enableAll != null) {
                context.resetPackages.add(pack);
            }
        });
    }

    @Override
    public void resetAll() {
        context.remove();
        init();
    }

    @Override
    public Optional<Boolean> isEnable(Class<?> clazz) {
        final SaveStateContext context = this.context.get();
        if (context == null || clazz == null) {
            return Optional.empty();
        }

        // Ищем перекрытие для конкретного класса
        if (context.classValues.containsKey(clazz)) {
            return Optional.of(context.classValues.get(clazz));
        }
        // Перед пакетами, нужно проверить не сброшен ли класс
        if (context.resetClasses.contains(clazz)) {
            return Optional.empty();
        }

        // Ищем подходящий пакет
        Optional<Boolean> result = context.packageValues.entrySet().stream()
                .filter(entry -> clazz.getPackage() == entry.getKey())
                .map(Map.Entry::getValue)
                .findFirst();
        if (result.isPresent()) {
            return result;
        }
        // Проверим не сброшен ли пакет
        if (context.resetPackages.contains(clazz.getPackage())) {
            return Optional.empty();
        }

        // enableAll имеет самый слабый приоритет, т.к. устанавливается перед классами и пакетами
        return Optional.ofNullable(context.enableAll);
    }

    /**
     * Инициализация контекста потока.
     * Внимание: Должно выполняться при любом обращении.
     */
    private void init() {
        if (context.get() == null) {
            context.set(new SaveStateContext());
        }
    }

    /**
     * Удаляет перекрытия для классов, присутствующих в пакете.
     * @param pack пакет.
     */
    private void removeClassesValuesFrom(Package pack) {
        context.get().classValues.keySet().stream()
                .filter(clazz -> clazz.getPackage() == pack)
                .collect(Collectors.toList())
                .forEach(clazz -> context.get().classValues.remove(clazz));
    }


    private void clearResetExcludes(Package pack) {
        context.get().resetPackages.remove(pack);
        context.get().resetClasses = context.get().resetClasses.stream()
                .filter(clazz -> clazz.getPackage() != pack)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private void clearResetExcludes(Collection<Class<?>> classes) {
        context.get().resetClasses.removeAll(classes);
    }

    private <T> List<T> toList(T head, T[] tail) {
        final List<T> list = new ArrayList<>(tail.length + 1);
        list.addAll(Arrays.asList(tail));
        list.add(head);
        return list;
    }
}
