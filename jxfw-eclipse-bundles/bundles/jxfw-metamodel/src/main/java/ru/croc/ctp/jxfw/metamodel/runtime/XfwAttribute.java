package ru.croc.ctp.jxfw.metamodel.runtime;

/**
 * Примитивное свойство или свойство-перечисление в метамодели.
 */
public interface XfwAttribute extends XfwStructuralFeature {

    /* EAttribute methods*/


    //boolean isId();


    /**
     * Тип атрибута в метамодели.
     * @return классификтаор типа.
     */
    XfwClassifier getEAttributeType();

    /* XFWAttribute methods*/

    /**
     * Returns the value of the '<em><b>Max Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Максимальное количество символов для значений строковых атрибутов
     * <!-- end-model-doc -->
     *
     * @return the value of the '<em>Max Length</em>' attribute.
     */
    int getMaxLength();


    /**
     * Считывание флага, активирующего генерацию дополнительных полей
     * для хранения параметров {@code XFWBlobInfo}.
     *
     * @return Значение флага. Если флаг в модели не найден, возвращается {@code false}
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     */
    boolean getGenerateBlobInfoFields();


    /**
     * Считывание суффикса дополнительного поля для хранения размера контента.
     *
     * @return Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     *                               или если параметр не найден в модели
     */
    String getContentSizeSuffix();


    /**
     * Считывание суффикса дополнительного поля для хранения имени файла.
     *
     * @return Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     *                               или если параметр не найден в модели
     */
    String getFileNameSuffix();


    /**
     * Считывание суффикса дополнительного поля для хранения типа контента.
     *
     * @return Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     *                               или если параметр не найден в модели
     */
    String getContentTypeSuffix();


    /**
     * Признак системного свойста ( идентификатор, версия).
     * @return да\нет
     */
    boolean isSystemField();


    /**
     * Признак свойста -идентификатора.
     * @return да\нет
     */
    boolean isIdField();

    /**
     * Признак свойста -версии.
     * @return да\нет
     */
    boolean isVersionField();




}
