package ru.croc.ctp.jxfw.cass.generator;

import static ru.croc.ctp.jxfw.core.generator.Constants.COMPLEX_KEY_ORDER;
import static ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.systemFields;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.ATTRIBUTE_PROPS_ANNOTATION_SOURCE;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.PRIMARY_KEY_ANNOTATION_SOURCE;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration;

import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext;
import ru.croc.ctp.jxfw.core.generator.impl.EcoreModelEmitterBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса формирования модели для CASS-хранилища.
 * <p/>
 * Created by SPlaunov on 08.08.2016.
 */
public class EcoreModelEmitterCass extends EcoreModelEmitterBaseImpl {

    /**
     * Конструктор.
     *
     * @param clazz   Класс доменной модели, для которого строится ecore-модель
     * @param context Контекст для доступа к вспомогательным сервисам xtend
     */
    public EcoreModelEmitterCass(ClassDeclaration clazz, PersistenceModuleContext context) {
        super(clazz, context);
    }

    @Override
    public void addKeyFieldDetails() {
        //т.к. генерируемый MutableClassDeclaration класс изменяется в сторону замены ключевых полей
        //на тип ключа (комплексный), то получается что для ecore модели нехватает полей,
        // поэтому подставляем исходный класс
        TypeDeclaration sourceRepresentaion = GeneratorHelperCass.findSourceRepresentaion(getClazz());

        List<? extends FieldDeclaration> partiotionKeysSorted
                = GeneratorHelperCass.partiotionKeysSorted(sourceRepresentaion);
        List<? extends FieldDeclaration> clusteringKeysSorted
                = GeneratorHelperCass.clusteringKeysSorted(sourceRepresentaion);

        //партиционные и кластерные вместе
        List<FieldDeclaration> keyFields = new ArrayList<>();
        keyFields.addAll(partiotionKeysSorted);
        keyFields.addAll(clusteringKeysSorted);

        //список полей класс для ecore модели
        //List<FieldDeclaration> fieldsList = ClassUtil.getFieldsList(sourceRepresentaion);

        // i будет порядком поля комплексного ключа в ecore и в model.js
        for (int i = 0; i < keyFields.size(); i++) {
            //из начальной модели
            FieldDeclaration keyFld = keyFields.get(i);

            //поле которое будем помечать как ключевое
            EStructuralFeature esField = getEcoreClass().getEStructuralFeature(keyFld.getSimpleName());

            if (esField == null) {
                throw new RuntimeException("Problem resolving keyFields for " + getClazz().getQualifiedName());
            }

            //Аннотация помечает поле как ключевое
            EAnnotation primaryKey = ECORE_FACTORY.createEAnnotation();
            primaryKey.setSource(PRIMARY_KEY_ANNOTATION_SOURCE.getUri());
            esField.getEAnnotations().add(primaryKey);

            // укажем в деталях аннотации порядок поля в комплексном ключе
            EAnnotation keyOrderAnn = ECORE_FACTORY.createEAnnotation();
            keyOrderAnn.setSource(ATTRIBUTE_PROPS_ANNOTATION_SOURCE.getUri());
            keyOrderAnn.getDetails().put(COMPLEX_KEY_ORDER, Integer.toString(i));
            esField.getEAnnotations().add(keyOrderAnn);
        }

    }

    @Override
    public boolean isFieldAddToModel(FieldDeclaration field) {
        return !(systemFields.contains(field.getSimpleName()) || "id".equals(field.getSimpleName()));
    }
}
