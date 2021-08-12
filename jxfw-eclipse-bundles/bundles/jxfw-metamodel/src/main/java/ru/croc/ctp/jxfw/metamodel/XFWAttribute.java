/**
 */
package ru.croc.ctp.jxfw.metamodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import javax.annotation.Nonnull;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XFW Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWAttribute#getMaxLength <em>Max Length</em>}</li>
 * </ul>
 *
 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWAttribute()
 * @model
 * @generated
 */
public interface XFWAttribute extends EObject, EAttribute {
    /**
	 * Returns the value of the '<em><b>Max Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Максимальное количество символов для значений строковых атрибутов
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Max Length</em>' attribute.
	 * @see #setMaxLength(int)
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWAttribute_MaxLength()
	 * @model
	 * @generated
	 */
    int getMaxLength();

    /**
	 * Sets the value of the '{@link ru.croc.ctp.jxfw.metamodel.XFWAttribute#getMaxLength <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Length</em>' attribute.
	 * @see #getMaxLength()
	 * @generated
	 */
    void setMaxLength(int value);

    /**
     * Установка флага, активирующего генерацию дополнительных полей
     * для хранения параметров {@code XFWBlobInfo}.
     *
     * @param generate Значение флага
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     */
    void setGenerateBlobInfoFields(boolean generate);

    /**
     * Считывание флага, активирующего генерацию дополнительных полей
     * для хранения параметров {@code XFWBlobInfo}.
     *
     * @return Значение флага. Если флаг в модели не найден, возвращается {@code false}
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     */
    boolean getGenerateBlobInfoFields();

    /**
     * Установка суффикса дополнительного поля для хранения размера контента.
     *
     * @param sizeSuffix Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     */
    void setContentSizeSuffix(String sizeSuffix);

    /**
     * Считывание суффикса дополнительного поля для хранения размера контента.
     *
     * @return Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     *                               или если параметр не найден в модели
     */
    @Nonnull
    String getContentSizeSuffix();

    /**
     * Установка суффикса дополнительного поля для хранения имени файла.
     *
     * @param fileNameSuffix Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     */
    void setFileNameSuffix(String fileNameSuffix);

    /**
     * Считывание суффикса дополнительного поля для хранения имени файла.
     *
     * @return Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     *                               или если параметр не найден в модели
     */
    @Nonnull
    String getFileNameSuffix();

    /**
     * Установка суффикса дополнительного поля для хранения типа контента.
     *
     * @param contentTypeSuffix Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     */
    void setContentTypeSuffix(String contentTypeSuffix);

    /**
     * Считывание суффикса дополнительного поля для хранения типа контента.
     *
     * @return Суффикс имени дополнительного поля
     * @throws IllegalStateException Если тип атрибута отличается от {@code java.sql.Blob}
     *                               или если параметр не найден в модели
     */
    @Nonnull
    String getContentTypeSuffix();


    /**
     * Признак системного свойста ( пока это только идентификатор и версия, остальные ситемные поля не
     * помещаются в метамодель).
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

} // XFWAttribute
