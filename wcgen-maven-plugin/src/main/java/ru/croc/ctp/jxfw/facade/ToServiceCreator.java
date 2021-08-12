package ru.croc.ctp.jxfw.facade;

import com.squareup.javapoet.JavaFile;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для создания сервисов трансформации
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public interface ToServiceCreator {

    /**
     * Фабричный метод для создания java-файла сервиса трансформации для переданной сущности.
     *
     * @param xfwClass - класс сущности для которой создается сервис
     * @param xfwClasses - сущности, прочтенные с модели
     * @param options  - набор опций для корректировки процесса создания
     * @return java-файл сервиса
     */
    List<JavaFile> create(XFWClass xfwClass, Set<XFWClass> xfwClasses, Map<String, Object> options);
}
