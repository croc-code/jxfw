package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.Set;

/**
 * Метамодель приложения.
 *
 * @since 1.5
 */
public interface XfwModel {

    /**
     * Найти в модели  все объекты (класс, перечисление) нужного типа
     *
     * @param type класс искомого (класс, перечисление)
     * @param <T>  тип искомого (класс, перечисление)
     * @return набор найденных объектов
     */
    <T extends XfwClassifier> Set<T> getAll(Class<T> type);


    /**
     * Известен ли данный тип метамодели.
     *
     * @param className тип ( полное или краткое имя)
     * @return да\нет
     */
    boolean isKnownType(String className);


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
    <T extends XfwClassifier> T findBySimpleName(String name, Class<T> type);

    /**
     * Найти в модели объект (класс, перечисление, датасорс) по полному имени
     *
     * @param name полное имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть, иначе null
     */
    <T extends XfwClassifier> T findByFqName(String name, Class<T> type);


    /**
     * Найти в модели объект (класс, перечисление, датасорс) по короткому или полному имени.
     *
     * @param name имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть, иначе null
     */
    <T extends XfwClassifier> T find(String name, Class<T> type);

    /**
     * Найти в модели объект (класс, перечисление, датасорс) по короткому имени.
     *
     * @param name имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть
     * @throws IllegalArgumentException если объект не найден
     */
    <T extends XfwClassifier> T findBySimpleNameThrowing(String name, Class<T> type);

    /**
     * Найти в модели объект (класс, перечисление, датасорс) по полному имени.
     *
     * @param name полное имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть
     * @throws IllegalArgumentException если объект не найден
     */
    <T extends XfwClassifier> T findByFqNameThrowing(String name, Class<T> type);


    /**
     * Найти в модели объект (класс, перечисление, датасорс) по короткому или полному имени.
     *
     * @param name имя
     * @param type класс искомого (класс, перечисление, датасорс)
     * @param <T>  тип искомого (класс, перечисление, датасорс)
     * @return объект, если есть
     * @throws IllegalArgumentException если объект не найден
     */
    <T extends XfwClassifier> T findThrowing(String name, Class<T> type);


    /**
     * Замена плейсхолдеров вида [ru.croc.ctp.domain.User] и
     * [ru.croc.ctp.domain.User#login] на строковый значения из метамодели.
     * @param input входящая строка
     * @param language язык для поиска имен в метамолели
     * @return преобразованная строка
     */
    String resolveMetadata(String input, String language);


    /**
     * Найти метаданные перечисления
     * @param clazz
     * @return метаданные
     * @throws IllegalArgumentException если метаданные не найдены
     */
    @SuppressWarnings("rawtypes")
	XfwEnumeration findEnum(Class<? extends Enum> clazz);


}