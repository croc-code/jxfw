package ru.croc.ctp.jxfw.fulltext.generator.solr;

import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.file.FileSystemSupport;
import org.slf4j.Logger;

import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil;
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import ru.croc.ctp.jxfw.fulltext.generator.XFWSearchClass;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Утилитный класс, анализатор исходников. Больше нужен для удбство дебага т.к. java класс проще дебажить в idea.
 *
 * @author SMufazzalov
 * @since 1.5
 */
public class SourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(SourceUtil.class);

    /**
     * Из xtend может быть получена базовая модель либо полнотекстовая, baseEntity это базовая. Но только для
     * базовой в итоге мы можем получить xtend представление.
     * @param elements элементы
     * @param transformationContext контекст
     * @return ключи базовая модель либо полнотекстовая модель, значения всегда базовая.
     */
    public static HashMap getMutableClzMap(
            List<? extends MutableClassDeclaration> elements,
            TransformationContext transformationContext
    ) {

        //ключи базовая модель либо полнотекстовая модель, значения всегда базовая.
        HashMap<MutableClassDeclaration, ClassDeclaration> result = new HashMap<>(elements.size());

        Consumer<MutableClassDeclaration> populateMap = baseEntity -> {
            //используется описание единой модели
            if (dual(baseEntity, transformationContext)) {
                //имя копии полнотекстовой сущности (в отличии от базовой, добавляется пакет в namespace)
                String name = GeneratorHelperSolr.getEntityQName(baseEntity, true);
                //поиск копии полнотекстовой сущности в контексте
                MutableClassDeclaration copyForFullTextEntity = transformationContext.findClass(name);
                if (copyForFullTextEntity == null) {
                    throw new RuntimeException("No class registered in separate package found for: "
                            + baseEntity.getQualifiedName());
                }
                result.put(copyForFullTextEntity, baseEntity);
            } else {
                result.put(baseEntity, baseEntity);
            }
        };

        elements.stream().forEach(populateMap);

        return result;
    }

    /**
     * Используется ли копия сущностей для полнотекста.
     * @param clazz сущность
     * @param ctx контекст
     * @return да в случае описания единой модели
     */
    public static boolean dual(ClassDeclaration clazz,
                               FileSystemSupport ctx) {
        return ClassUtil.existAnnotation(clazz, XFWSearchClass.class)
                && ClassUtil.existAnnotation(clazz, XFWObject.class);
    }

}
