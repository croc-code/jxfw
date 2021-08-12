package ru.croc.ctp.jxfw.core.generator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider;

import ru.croc.ctp.jxfw.metamodel.XfwValueType;

import java.util.List;

/**
 * Общий интерфейс для модулей кодогенератора для генерации кода работы с хранилищами данных.
 *
 * @see PersistenceModuleBaseImpl
 * @see ru.croc.ctp.jxfw.jpa.generator.PersistenceModuleJpa
 * @see ru.croc.ctp.jxfw.cass.generator.PersistenceModuleCass
 * @see ru.croc.ctp.jxfw.solr.generator.PersistenceModuleSolr
 * @see ru.croc.ctp.jxfw.cmis.generator.PersistenceModuleCmis
 * @since 1.0
 */
@SuppressWarnings("JavadocReference")
public interface PersistenceModule {

    /**
     * Возвращает сервис формирования ecore-модели.
     *
     * @return сервис формирования ecore-модели
     */
    EcoreModelEmitter getEcoreModelEmitter();

    /**
     * сгенерировать дополнительные необходимые поля в зависимости от типа модуля.
     */
    void produceRequiredFields();

    /**
     * @return Аннотация которой должно быть проаннотировано поле для первичного ключа.
     */
    TypeReference getIdType();

    /**
     * @return TypeReference типа первичного ключа.
     */
    TypeReference getKeyType();

    /**
     * Имя типа для первичного ключа для класса clazz.
     *
     * @param clazz класс для которого необходимо вывести тип
     * @return simpleName типа
     */
    String getKeyType(ClassDeclaration clazz);

    /**
     * Регистрация всех необходимых классов: сервисы, репозитории, сущности, компараторы и т.д.
     *
     * @param clazz класс для которого необходимо сгенерировать дополнительные классы
     */
    void registerClasses(ClassDeclaration clazz);

    /**
     * Наследование Entity класса в зависимости от типа хранилища.
     */
    void extendClazz();

    /**
     * Получить только поля данного класса.
     * @return список полей
     */
    List<FieldDeclaration> getFieldsList();

    /**
     * Создание комплексного ключа для сущности.
     */
    void createComplexKey();

    /**
     * Создание интерфейса "репозиторий" для сущности в зависимости от типа хранилища.
     */
    void createRepository();

    /**
     * Создание реализации интерфейса "репозиторий" для сущности в зависимости от типа хранилища.
     */
    void createRepositoryImpl();

    /**
     * Создание класса-сервиса для сущности.
     */
    void createService();

    /**
     * Добавить в ecore описание свойств поля.
     *
     * @param feature     структурный элемент - поле
     * @param annotations список аннотаций поля
     * @param context     контекст {@link TypeReferenceProvider}
     */
    void ecoreAddColumnProps(EStructuralFeature feature,
                             Iterable<? extends AnnotationReference> annotations,
                             TypeReferenceProvider context,
                             XfwValueType xfwValueType);

    /**
     * Установить свойство Opposite для поля.
     *
     * @param field          поля
     * @param refClass       ссылка на класс
     * @param mutableClasses список всех классов
     * @param context        контекст
     * @return объект {@link EReference}
     */
    EReference ecoreGetOpposite(FieldDeclaration field, EClass refClass,
                                List<MutableClassDeclaration> mutableClasses,
                                TypeReferenceProvider context);

    /**
     * Проверка корректности кода модели доменного типа.
     */
    void doValidate();

    /**
     * Участие модуля в генерации (скрипты и тд).
     */
    void doGenerateCode();

    /**
     * Проводит проверку корректности классов перед трансформацией.
     */
    void validateBeforeTransform();

    /**
     * Расширяет абстрактный класс используемый для мапппинга общих полей сущностей
     */
    void extendMappedSuperclass();
}
