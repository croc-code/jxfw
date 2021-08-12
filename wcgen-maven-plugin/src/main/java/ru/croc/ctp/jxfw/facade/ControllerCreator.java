package ru.croc.ctp.jxfw.facade;

import com.squareup.javapoet.JavaFile;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для создания различных видов контроллеров.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public interface ControllerCreator {

    /**
     * Фабричный метод для создания java-файла контроллера для переданной сущности.
     *
     * @param xfwClass - класс сущности для которой создается контроллер
     * @param options  - набор опций для корректировки процесса создания
     * @return java-файлы контроллеров
     */
    List<JavaFile> create(XFWClass xfwClass, Map<String, Object> options);
}
