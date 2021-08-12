package ru.croc.ctp.jxfw.core.xtend.logging;

import org.slf4j.Logger;

import ru.croc.ctp.jxfw.core.xtend.logging.impl.LoggerOverReflection;

/**
 * Фабрика логгеров. Создает логгеры, учитывающие контекст исполнения - Maven, Eclipse или IDEA.
 * Все логирование в коде процессовров Xtend Active Annotation должно выполняться исключительно с помощью таких
 * логгеров.
 * <p/>
 * Created by SPlaunov on 04.07.2016.
 */
public class LoggerFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final Class<?> log4jLoggerClass;

    static {
        Class<?> log4jLoggerClassTemp = null;
        if (isIdeaDetected()) {
            try {
                log4jLoggerClassTemp = CLASS_LOADER.loadClass("org.apache.log4j.Logger");
            } catch (ClassNotFoundException e) {
                //Возможная ситуация, log4j может не быть
                log4jLoggerClassTemp = null;
            }
        }
        log4jLoggerClass = log4jLoggerClassTemp;
    }

    /**
     * Определяет, выполняется код плагином IntelliJ IDEA или нет. Для этого просматривает FQN всех класс-лоадеров и
     * ищет подстроку "idea"
     * <p/>
     *
     * @return Возвращает {@code true}, если код выполняется под плагином IDEA
     */
    private static boolean isIdeaDetected() {
        ClassLoader classLoader = LoggerFactory.CLASS_LOADER;
        while (classLoader != null) {
            String classLoaderClassName = classLoader.getClass().getName().toLowerCase();
            if (classLoaderClassName.contains("idea") || classLoaderClassName.contains("intellij")) {
                return true;
            }
            classLoader = classLoader.getParent();
        }
        return false;
    }

    /**
     * Возвращает логгер с имененем, равным FQN переданного класса.
     * <p/>
     *
     * @param clazz Класс для формирования имени логгера
     * @return Логгер с именем, равным FQN переданного класса
     */
    public static Logger getLogger(Class<?> clazz) {
        if (log4jLoggerClass != null) {
            try {
                Object logger = log4jLoggerClass.getMethod("getLogger", Class.class).invoke(null, clazz);
                return new LoggerOverReflection(logger);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
}
