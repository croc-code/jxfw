package ru.croc.ctp.jxfw.facade.transfer;

import static org.apache.commons.lang3.ClassUtils.getShortClassName;
import static org.springframework.cglib.core.TypeUtils.getPackageName;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import org.eclipse.emf.common.util.EList;
import ru.croc.ctp.jxfw.facade.transfer.jpa.TransferToServiceGeneratorJpa;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Создаёт сервисы трансформации для фасада модуля Transfer.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class TransferToServiceCreator implements ToServiceCreator {
    @Override
    public List<JavaFile> create(XFWClass xfwClass, Map<String, Object> options) {
        //TODO насколько целесообразно генерировать To сервисы для импорта данных
        if (xfwClass.isTransientType()) {
            return Collections.emptyList();
        }

        final List<JavaFile> result = new ArrayList<>();
        final EList<String> persistenceModules = xfwClass.getPersistenceModule();

        persistenceModules.forEach(persistenceModule -> {
            final TransferToServiceGenerator<TypeName> toServiceGenerator;
            final String keyTypeName = xfwClass.getKeyTypeName();
            final TypeName keyType = ClassName.get(getPackageName(keyTypeName), getShortClassName(keyTypeName));

            switch (persistenceModule) {
                case "JPA":
                    toServiceGenerator = new TransferToServiceGeneratorJpa<>(xfwClass, persistenceModule, keyType);
                    break;
                //TODO CASS, CMIS, FULLTEXT, SOLR
                default:
                    toServiceGenerator = new TransferToServiceGenerator<>(xfwClass, persistenceModule, keyType);
            }
            result.add(toServiceGenerator.generate());
        });
        return result;
    }
}
