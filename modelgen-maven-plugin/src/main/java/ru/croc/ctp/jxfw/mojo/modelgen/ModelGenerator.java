package ru.croc.ctp.jxfw.mojo.modelgen;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.impl.ModelHelper;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Генератор модели.
 *
 * @author AKogun
 */
public class ModelGenerator {

    private static final List<String> IMPORT_EXCLUDES = new ArrayList<String>();

    private File outputDirectory;
    private String basePackage;
    private Log log;
    private VelocityEngine ve;
    private XFWModelImpl xfwModel;

    private final Map<String, PackageModel> allPackageModels = new HashMap<String, PackageModel>();

    /**
     * Конструтор.
     *
     * @param sourceModelFile файл исходной модели
     * @param outputDirectory дирректория в которую должен быть размещен результат
     * @param basePackage     базовый пакет для генерируемой модели
     * @param log             логгер
     */
    public ModelGenerator(File sourceModelFile, File outputDirectory,
                          String basePackage, Log log) {

        IMPORT_EXCLUDES.add("java.lang");

        this.outputDirectory = outputDirectory;
        this.basePackage = basePackage;
        this.log = log;

        xfwModel = new XFWModelImpl(sourceModelFile.toPath());
    }

    /**
     * Выполнение генерации.
     */
    public void process() {

        Properties props = new Properties();
        URL url = this.getClass().getClassLoader()
                .getResource("velocity.properties");
        try {
            if (url == null) {
                throw new IllegalStateException("Failed to load velocity.properties");
            }

            props.load(url.openStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ve = new VelocityEngine(props);
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.Log4JLogChute");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velogger");
        ve.init();

        preProcess();

        allPackageModels.forEach((fqn, pm) -> pm.generate());

    }

    private void preProcess() {
        xfwModel.getAll(EClassifier.class).forEach(xfwClass -> xfwClass.setInstanceTypeName(""));
        for (XFWPackage pckg : xfwModel.getPackages()) {
            if (pckg.getAll(XFWClass.class).size() > 0) {
                PackageModel packageModel = new PackageModel(pckg);

                allPackageModels.put(packageModel.getFqName(), packageModel);

                packageModel.classes.addAll(packageModel.pckg.getAll(XFWClass.class));

            }
        }

    }

    /**
     * Encapsulates all data to generate single xtend-file.
     */
    public class PackageModel {
        private final XFWPackage pckg;
        private Set<String> imports;
        private final Set<XFWClass> classes = new HashSet<XFWClass>();
        private String fqName;

        /**
         * Конструктор.
         *
         * @param pckg пакет, содержащий классы модели
         */
        public PackageModel(XFWPackage pckg) {
            this.pckg = pckg;
        }

        /**
         * Добавить класс к модели.
         *
         * @param type добавляемый класс
         * @return true в случае успеха, false, если класс с таким наименованием уже был добавлен ранее
         */
        public boolean addClass(XFWClass type) {
            if (classes.stream().anyMatch((cl) -> cl.getName().equals(type.getName()))) {
                return false;
            }

            classes.add(type);
            return true;
        }

        /**
         * Получить FQ наименование для пакета.
         *
         * @return FQ наименование
         */
        public String getFqName() {
            if (fqName != null) {
                return fqName;
            }

            return ModelUtils.getFqName(pckg, basePackage);
        }


        /**
         * Генерация файла модели.
         */
        public void generate() {

            /*
            Если модель имеет структруру jxfwmodel->subpackages->classes,
            то имя xtend , будет jxfwmodel (jXFW ecore generation)
            Если модель имеет структруру datamodel->classes ,
            то имя xtend , будет datamodel (xmi->ecore transformator)

             */
            String xtendName = pckg.getName();
            if (pckg.getESuperPackage() != null) {
                xtendName = pckg.getESuperPackage().getName();
            }
            File output = Paths.get(outputDirectory.getPath(),
                    getFqName().replace('.', File.separatorChar),
                    xtendName + ".xtend").toFile();

            if (!output.exists()) {
                output.getParentFile().mkdirs();
            }

            VelocityContext vc = new VelocityContext();
            vc.put("packageModel", this);

            try (Writer w = new OutputStreamWriter(
                    new FileOutputStream(output), StandardCharsets.UTF_8)) {
                Template vt = ve.getTemplate("model.vm");
                vt.merge(vc, w);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            log.info("Generated file: " + output);
        }

        /**
         * Получить список всех записей import для файла модели.
         *
         * @return список записей import
         */
        @SuppressWarnings("unused")
        public List<String> getImports() {
            if (imports == null) {
                imports = new HashSet<String>();

                for (XFWClass cl : classes) {
                    for (EAttribute attr : cl.getEAttributes()) {
                        String tn = getAttrTypeName(attr, true);
                        if (!tn.isEmpty()) {
                            imports.add(tn);
                        }
                    }
                    if (hasSuperType(cl)) {
                        String tn = getTypeName(cl.getESuperTypes().get(0), true);
                        if (!tn.isEmpty()) {
                            imports.add(tn);
                        }
                    }
                }
            }

            return imports.stream().sorted((s1, s2) -> s1.compareTo(s2))
                    .collect(Collectors.toList());
        }


        @SuppressWarnings("unused")
        public List<EClassifier> getClasses() {
            return classes
                    .stream()
                    .sorted((EClassifier c1, EClassifier c2) -> c1.getName()
                            .compareTo(c2.getName()))
                    .collect(Collectors.toList());
        }

        /**
         * Проверка, является ли свойство ссылочным.
         *
         * @param structuralFeature Объект свойства
         * @return да или нет
         */
        @SuppressWarnings("unused")
        public boolean isReference(EStructuralFeature structuralFeature) {
            return structuralFeature instanceof EReference;
        }

        /**
         * Получение аттрибутов аннотации интернационализации.
         *
         * @param element объект свойства
         * @return набор значений атрибутов аннотации
         */
        @SuppressWarnings("unused")
        public Set<Entry<String, String>> getLabelEntries(EModelElement element) {
            EAnnotation labelAnnotation = element.getEAnnotation(XFWConstants.I18N_ANNOTATION_SOURCE.getUri());
            return labelAnnotation != null ? labelAnnotation.getDetails().entrySet() : null;
        }


        /**
         * javax.persistence.Table annotation from ecore
         */
        private static final String TABLE_ANNOTATION_SOURCE = "http://www.croc.ru/ctp/model/table";

        /**
         * Проверка наличия аннотации @Table.
         *
         * @param element объект свойства
         * @return значение наименования таблицы
         */
        @SuppressWarnings("unused")
        public String getTableName(EModelElement element) {
            EAnnotation annotation = element.getEAnnotation(TABLE_ANNOTATION_SOURCE);
            if (annotation != null) {
                return annotation.getDetails().get("name");
            }
            return null;
        }

        /**
         * Проверка наличия аннотации @JoinTable.
         *
         * @param element объект свойства
         * @return значение наименования таблицы
         */
        @SuppressWarnings("unused")
        public String getJoinTableName(EModelElement element) {
            EAnnotation annotation = element.getEAnnotation(XFWConstants.JOINTABLE_ANNOTATION_SOURCE.getUri());
            if (annotation != null) {
                return annotation.getDetails().get("name");
            }
            return null;
        }

        /**
         * Проверка наличия аннотации валидации по шаблону.
         *
         * @param element объект свойства
         * @return да или нет
         */
        @SuppressWarnings("unused")
        public boolean hasPatternValidation(EModelElement element) {
            EAnnotation annotation = element.getEAnnotation(
                    XFWConstants.PATTERN_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri());
            if (annotation != null) {
                return StringUtils.isNotBlank(annotation.getDetails().get("regexp"));
            }
            return false;
        }

        /**
         * Получение аттрибутов аннотации валидации по шаблону.
         *
         * @param element объект свойства
         * @return набор значений атрибутов аннотации
         */
        @SuppressWarnings("unused")
        public Set<Entry<String, String>> getPatternEntries(EModelElement element) {
            return element.getEAnnotation(XFWConstants.PATTERN_VALIDATED_FIELD_ANNOTATION_SOURCE.getUri())
                    .getDetails().entrySet();
        }

        /**
         * Получение наименования типа сойства для перечислений.
         *
         * @param structuralFeature объект свойства
         * @return наименование типа или null, если не является перечислением
         */
        public String getEnumeratedClass(EStructuralFeature structuralFeature) {
            String result = null;
            EClassifier type = structuralFeature.getEType();
            if (type instanceof EEnum) {
                result = type.getName();
            }
            return result;
        }

        /**
         * Проверка, является ли тип свойства перечислением.
         *
         * @param structuralFeature объект свойства
         * @return да или нет
         */
        @SuppressWarnings("unused")
        public boolean isEnumerated(EStructuralFeature structuralFeature) {
            return getEnumeratedClass(structuralFeature) != null;
        }

        /**
         * Проверка, есть ли у класса родительский класс.
         * @param xfwClass - класс
         * @return да или нет
         */
        @SuppressWarnings("unused")
        public boolean hasSuperType(XFWClass xfwClass) {
            return xfwClass.getESuperTypes().size() > 0;
        }

        /**
         * Получить имя родительского класса.
         * @param xfwClass - класс
         * @return имя
         */
        @SuppressWarnings("unused")
        public String getSuperType(XFWClass xfwClass) {
            return hasSuperType(xfwClass) ? getTypeName(xfwClass.getESuperTypes().get(0), false) : "";
        }

        /**
         * Получение наименования типа свойства.
         *
         * @param structuralFeature объект свойства
         * @param forImports        добавлять данные о типе в import
         * @return наименование типа
         */
        public String getAttrTypeName(EStructuralFeature structuralFeature, Boolean forImports) {

            return getTypeName(structuralFeature.getEType(), forImports);

        }
        @SuppressWarnings("unused")
        public boolean isEnumFlags(EStructuralFeature structuralFeature){
            return structuralFeature.isMany()&& structuralFeature.getEType() instanceof  EEnum;
        }

        private String getTypeName(EClassifier type, Boolean forImports) {
            String tn = type.getInstanceTypeName();
            String fq = "";

            if (tn != null && !tn.isEmpty()) {
                int lastDot = tn.lastIndexOf('.');
                if (lastDot != -1) {
                    fq = tn.substring(0, lastDot);
                    tn = tn.substring(lastDot + 1);
                }
            } else {
                fq = ModelUtils.getFqName(type.getEPackage(), basePackage);
                tn = type.getName();
            }

            final String s = tn;
            final String f = fq;

            if (!fq.equals(getFqName())) {
                if (forImports) {
                    if (IMPORT_EXCLUDES.stream().filter(ie -> f.startsWith(ie)).count() > 0) {
                        tn = "";
                    } else if (!classes.stream().anyMatch(cl -> cl.getName().equals(s))) {
                        tn = fq.concat(".").concat(s);
                    }
                } else {
                    if (classes.stream().anyMatch(cl -> cl.getName().equals(s))) {
                        tn = fq.concat(".").concat(s);
                    }
                }
            } else if (forImports) {
                tn = "";
            }

            return tn;

        }
    }

}
