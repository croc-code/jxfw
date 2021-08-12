package ru.croc.ctp.jxfw.facade.transfer;

import com.squareup.javapoet.JavaFile;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для создания сервисов трансформации.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public interface ToServiceCreator {

    /**
     * Фабричный метод для создания java-файла сервиса трансформации для переданной сущности.
     *
     * @param xfwClass - класс сущности для которой создается сервис
     * @param options  - набор опций для корректировки процесса создания
     * @return java-файл сервиса
     */
    List<JavaFile> create(XFWClass xfwClass, Map<String, Object> options);
}
