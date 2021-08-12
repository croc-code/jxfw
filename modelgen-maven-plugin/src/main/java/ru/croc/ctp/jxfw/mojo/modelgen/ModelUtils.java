package ru.croc.ctp.jxfw.mojo.modelgen;

import org.eclipse.emf.ecore.EPackage;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;

/**
 * Утилитарный класс для плагина генерации модели.
 *
 * @author AKogun
 */
public class ModelUtils {

    /**
     * Получение FQ наименования для пакета.
     *
     * @param pckg        Объект пакета
     * @param basePackage Наименование базового пакета
     * @return FQ наименование пакета
     */
    public static String getFqName(EPackage pckg, String basePackage) {
        if (pckg == null) {
            return "no_package";
        }

        if (basePackage.isEmpty()) {
            return pckg.getName().toLowerCase();
        } else {
            return basePackage.concat(".").concat(pckg.getName())
                    .toLowerCase();
        }
    }

}
