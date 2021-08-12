package ru.croc.ctp.jxfw.transfer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.util.CustomizableThreadCreator;
import ru.croc.ctp.jxfw.transfer.TransferService;
import ru.croc.ctp.jxfw.transfer.wc.OperationStateInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.croc.ctp.jxfw.transfer.wc.OperationStatus.*;

/**
 * Расширение {@link SimpleAsyncTaskExecutor} добавляющее возможность остановить поток по его ид.
 * Note: Важен порядок операций класса.
 *  1) Вызываем setCurrentId из потока исполнения, который будет выполнять execute()
 *  2) Вызываем execute()
 *  3) Из любого места(кроме останавливаемого потока) можно вызвать stop() передав ид.
 *
 * Остановка вызывает interrupt на потоке соответствующему ид.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class TaskExecutorWithInterrupt extends CustomizableThreadCreator implements TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(TaskExecutorWithInterrupt.class);
    /**
     * Ид текущей операции в этом потоке.
     */
    private final ThreadLocal<String> currentOperationId = new ThreadLocal<>();
    /**
     * Сопоставление ид операции потоку.
     */
    private final Map<String, ThreadState> operations = new ConcurrentHashMap<>();
    /**
     * Поток управляющий остановкой и очисткой рабочих потоков.
     */
    private final Thread threadManager;
    /**
     * Статус завершения всех потоков.
     */
    private final AtomicBoolean shutdown = new AtomicBoolean(false);


    /**
     * Расширение {@link SimpleAsyncTaskExecutor} добавляющее возможность остановить поток по его ид.
     *
     * @param transferService сервис трансфер.
     * @param pollingInterval интервал опроса сервиса(секунд).
     */
    public TaskExecutorWithInterrupt(TransferService transferService, int pollingInterval) {
        threadManager = createThread(() -> {
            long lastPollingTime = System.currentTimeMillis();

            while (!operations.isEmpty() || !shutdown.get()) {
                // Опрос сервиса о наличие завершенных задач
                if (System.currentTimeMillis() - lastPollingTime > pollingInterval * 1000) {
                    try {
                        operations.entrySet().stream()
                                .filter(operation -> operation.getValue().active.get())
                                .filter(operation -> Optional.ofNullable(transferService.getStatus(operation.getKey()))
                                        .map(OperationStateInfo::getStatus)
                                        .map(status -> status == Aborted || status == Aborting)
                                        .orElse(true))
                                .forEach(operation -> stop(operation.getKey()));
                        lastPollingTime = System.currentTimeMillis();
                    } catch (RuntimeException e) {
                        log.error("The status of operations is not available.", e);
                    }
                }

                // Пытаемся завершить прерванные задачи
                final List<String> trash = new ArrayList<>();
                for (Map.Entry<String, ThreadState> entry : operations.entrySet()) {
                    final ThreadState state = entry.getValue();

                    if (!state.thread.isAlive()) {
                        trash.add(entry.getKey());
                        log.debug(String.format("Thread %s is stopped.", state.thread));
                    } else if (!state.active.get()) { // пытаемся прервать потоки помеченные на остановку
                        state.thread.interrupt();
                        log.debug(String.format("Thread %s is interrupt.", state.thread));
                    }
                }

                // удаляем из памяти уже остановленные потоки
                for (String key : trash) {
                    operations.remove(key);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.debug("Manager thread is interrupted!");
                    return;
                }
            }
        });
        threadManager.setDaemon(true);
        threadManager.setPriority(Thread.MAX_PRIORITY);
        threadManager.start();
    }

    @Override
    public synchronized void execute(Runnable task) {
        if (shutdown.get()) {
            throw new TaskRejectedException("TaskExecutor is shutdown.");
        }

        final String operationId = currentOperationId.get();
        if (operationId == null) {
            // перед выполнением метода нужно установить текущий ид из текущего потока исполнения
            throw new TaskRejectedException("Current operation id is not set.");
        }

        operations.put(operationId, new ThreadState(task));
    }

    /**
     * Вызывает interrupt на потоке соответсвуем переданному ид.
     * @param operationId ид потока.
     */
    public synchronized void stop(String operationId) {
        final ThreadState threadState = operations.get(operationId);
        if (threadState != null) {
            threadState.stop();
        }
    }

    /**
     * Вызывает interrupt на потоке соответствующему переданному ид.
     * @param operationId ид потока.
     * @throws IllegalArgumentException если для указанного ид поток не найден.
     */
    public synchronized boolean isAlive(String operationId) {
        final ThreadState thread = operations.get(operationId);
        return thread != null ? thread.thread.isAlive() : false;
    }

    /**
     * Устанавливает ид для потока, который будет запущен следующим из этого потока.
     * @param id ид операции.
     */
    public synchronized void setCurrentOperationId(String id) {
        currentOperationId.set(id);
    }

    /**
     * Останавливает все потоки.
     * Note: операция происходит несинхронно.
     */
    public synchronized void shutdown() {
        for (ThreadState threadState : operations.values()) {
            threadState.stop();
        }
        shutdown.set(true);
    }

    /**
     * Подсчитывает количество живых потоков.
     * @return количество живых потоков.
     */
    public synchronized int numberOfAliveThreads() {
        int count = 0;
        for (ThreadState state : operations.values()) {
            if (state.thread.isAlive()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Состояние потока.
     */
    private class ThreadState {
        final Thread thread;
        final AtomicBoolean active;

        /**
         * Создаёт поток для задачи и запускает его.
         * @param task задача.
         */
        ThreadState(Runnable task) {
            this.thread = createThread(task);
            this.active = new AtomicBoolean(true);
            thread.start();
            log.debug("Create new thread: " + thread);
        }

        /**
         * Помечает поток для его остановки.
         */
        void stop() {
            thread.interrupt();
            active.set(false);
        }
    }
}
