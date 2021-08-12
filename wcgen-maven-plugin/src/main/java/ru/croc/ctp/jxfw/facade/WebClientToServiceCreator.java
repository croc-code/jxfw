package ru.croc.ctp.jxfw.facade;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.ClassUtils.getPackageName;
import static org.apache.commons.lang.ClassUtils.getShortClassName;
import static ru.croc.ctp.jxfw.metamodel.impl.ModelHelper.useFulltext;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import org.eclipse.emf.common.util.EList;
import ru.croc.ctp.jxfw.core.generator.StorageType;
import ru.croc.ctp.jxfw.facade.cass.ToServiceGeneratorCass;
import ru.croc.ctp.jxfw.facade.cmis.ToServiceGeneratorCmis;
import ru.croc.ctp.jxfw.facade.jpa.ToServiceGeneratorJpa;
import ru.croc.ctp.jxfw.facade.solr.ToServiceGeneratorSolr;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Класс для создания контроллера для WebClient.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class WebClientToServiceCreator implements ToServiceCreator {


    @Override
    public List<JavaFile> create(XFWClass xfwClass, Set<XFWClass> xfwClasses, Map<String, Object> options) {
        final List<JavaFile> result = newArrayList();
        final EList<String> persistenceModules = xfwClass.getPersistenceModule();

       if (xfwClass.isTransientType()) {
            final String keyTypeName = xfwClass.getKeyTypeName();
            final TypeName keyType = ClassName.get(getPackageName(keyTypeName), getShortClassName(keyTypeName));
            TransientXfwObjectToGenerator generator =
                    new TransientXfwObjectToGenerator(xfwClass, xfwClasses, null, keyType);
            result.add(generator.generate());
        } else {
            persistenceModules.forEach(persistenceModule -> {
                final ToServiceGenerator<TypeName> toServiceGenerator;
                final String keyTypeName = xfwClass.getKeyTypeName();
                final TypeName keyType = ClassName.get(getPackageName(keyTypeName), getShortClassName(keyTypeName));

                switch (persistenceModule) {
                    case "JPA":
                        toServiceGenerator = new ToServiceGeneratorJpa<>(xfwClass, xfwClasses, persistenceModule, keyType);
                        break;
                    case "SOLR":
                        toServiceGenerator =
                                new ToServiceGeneratorSolr<>(xfwClass, xfwClasses, persistenceModule, keyType);
                        break;
                    case "CASS":
                        toServiceGenerator =
                                new ToServiceGeneratorCass<>(xfwClass, xfwClasses, persistenceModule, keyType);
                        break;
                    case "CMIS":
                        toServiceGenerator =
                                new ToServiceGeneratorCmis<>(xfwClass, xfwClasses, persistenceModule, keyType);
                        break;
                    default:
                        toServiceGenerator =
                                new ToServiceGenerator<>(xfwClass, xfwClasses, persistenceModule, keyType);
                }
                //TOсервис "родной" сущностей базового хранилища
                result.add(toServiceGenerator.generate());
                //в случае если используются возможности полнотекста, нужен ТО сервис для полнотекста
                if (useFulltext(xfwClass)) {
                    ToServiceGeneratorSolr toServiceGeneratorSolr = new ToServiceGeneratorSolr(xfwClass, xfwClasses,
                            StorageType.SOLR.name(), true);
                    JavaFile fullTextToServiceFile = toServiceGeneratorSolr.generate();
                    //"fulltext" в отдельном пакете
                    result.add(fullTextToServiceFile);
                }
            });
        }
        return result;
    }
}
