package ru.croc.ctp.jxfw.cli.watch;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Задача аккумулировать события поступившее с небольшим промежутком по времени
 * и с некоторой задержкой позвать обработчик.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
public class EventsAccumulator {

    private Set<File> toBeProcessed = Collections.synchronizedSet(new HashSet<>());
    private Consumer<Collection<File>> onSignal;
    private ScheduledFuture<?> scheduledFuture = null;
    private ScheduledExecutorService scheduler = getScheduledExecutorService();

    /**
     * Конструктор.
     * @param onSignal обработчик
     */
    public EventsAccumulator(Consumer<Collection<File>> onSignal) {
        this.onSignal = onSignal;
    }

    /**
     * Добавить коллекцию измененных файлов.
     * @param changedFiles измененные файлы
     */
    public void add(Collection<File> changedFiles) {
        if (changedFiles == null || changedFiles.isEmpty()) {
            return;
        }
        //прибиваем предыдущее намерение компилировать, в связи с новопоступившими событиями
        if (scheduledFuture != null && scheduledFuture.getDelay(TimeUnit.MICROSECONDS) > 0) {
            scheduledFuture.cancel(false);
        }
        toBeProcessed.addAll(changedFiles);
        scheduledFuture = scheduler.schedule(() -> {
            onSignal.andThen(x -> toBeProcessed.clear()).accept(new HashSet<>(toBeProcessed));
        }, 1, TimeUnit.SECONDS);
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread newThread = Executors.defaultThreadFactory().newThread(r);
            newThread.setDaemon(true);
            newThread.setName("events-accumulator");
            return newThread;
        });
    }
}