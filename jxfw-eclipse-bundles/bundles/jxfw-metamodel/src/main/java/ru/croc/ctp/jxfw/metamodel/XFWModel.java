package ru.croc.ctp.jxfw.metamodel;

import org.eclipse.emf.ecore.EClassifier;

import java.util.Set;

/**
 * Метамодель приложения.
 *
 * @since 1.5
 */
public interface XFWModel {

    /**
     * Найти в модели  все объекты (класс, перечисление, датасорс) нужного типа
     *
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return набор найденных объектов
     */
    <T extends EClassifier> Set<T> getAll(Class<T> type);


    /**
     * Набор языков, существующих в данной модели.
     *
     * @return набор языков.
     */
    Set<String> getAvailableLanguages();

    /**
     * Найти в модели объект (класс, перечисление, датасорс) по короткому имени.
     *
     * @param name имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть, иначе null
     */
    <T extends EClassifier> T findBySimpleName(String name, Class<T> type);

    /**
     * Найти в модели объект (класс, перечисление, датасорс) по полному имени
     *
     * @param name полное имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть, иначе null
     */
    <T extends EClassifier> T findByFqName(String name, Class<T> type);


    /**
     * Найти в модели объект (класс, перечисление, датасорс) по короткому или полному имени.
     *
     * @param name имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть, иначе null
     */
    <T extends EClassifier> T find(String name, Class<T> type);




}