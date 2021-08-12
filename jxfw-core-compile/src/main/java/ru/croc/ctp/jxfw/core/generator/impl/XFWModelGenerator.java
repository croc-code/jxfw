package ru.croc.ctp.jxfw.core.generator.impl;

import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static ru.croc.ctp.jxfw.core.generator.AbstractEcoreGenerator.XFWMM_FACTORY;

import com.google.common.collect.Maps;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.slf4j.Logger;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Имплементация модели, которая умеет создавать модель, добавлять в нее сущности
 * и сохранять.
 * Created by OKrutova on 18.07.2017.
 */
public class XFWModelGenerator extends XFWModelImpl {

    private final Logger logger;

    private static final String JXFW_MODEL_ROOT_PACKAGE = "jxfwmodel";

    /**
     * Корневой пакет модели, в которую будем писать новые классы.
     */
    private XFWPackage rootPackage;


    /**
     * Основной ресурс, в который происходит запись новых классов.
     */
    private Resource mainResource;

    /**
     * Семафор для синхронизации потоков.
     */
    public static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();

    /**
     * Конструктор, создающий новую модель или загружающий существующую.
     * Используется в генераторе ecore-моделей.
     *
     * @param projectJavaUri - путь к модели
     */
    public XFWModelGenerator(java.net.URI projectJavaUri, Logger logger) {

        this.logger = logger;

        java.net.URI modelJavaUri = projectJavaUri.resolve(XFWMMPackage.eINSTANCE.getModelFileUri(projectJavaUri));

        String modelStringPath = modelJavaUri.getPath();
        URI modelEmfUri = URI.createFileURI(modelStringPath);

        File modelFile = new File(modelStringPath);
        logger.debug("Path to check the model file existence: " + modelStringPath);
        logger.debug("URI to create/read the model: " + modelEmfUri);



        /*
          JXFW-901 Сейчас все ecore-модели загружаются при компиляции каждого файла xtend заново.
          Рассмотреть возможность грузить их при компиляции первого xtend и дальше использовать уже загруженные модели.
          ---------------------
          Посмотрела сколько времени выигрывается от такой оптимизации.
          Получилось, что нисколько. На фоне общего времени сборки порядка 45 секунд и 18 файлах xtend, выигрыша не видно.
          Поэтому данный код оставлен без изменений и оптимизаций.
         */

        File modelDir = modelFile.getParentFile();
        loadModelsFromDir(modelDir.toPath());

        if (modelFile.exists()) {
            logger.debug("Model exists");
            /*
                Если модель существует, то ее уже загрузили.
                Надо ее найти в resourceSet.
             */
            rootPackage = null;
            resourceSet.getResources().forEach(resource -> {
                if (resource.getURI().equals(modelEmfUri)) {
                    rootPackage = getRootPackage(resource);
                    mainResource = resource;
                }
            });
            if (rootPackage == null) {
                throw new IllegalStateException("No root package in model");
            }

        } else {
            logger.debug("Model doesn't exist");
            mainResource = resourceSet.createResource(modelEmfUri);
            rootPackage = createRootPackage(mainResource);
        }
    }


    /**
     * Метод сохранения сформированной модели.
     */
    public void save() {
        try {
            REENTRANT_LOCK.lock();

            Map<String, String> options = Maps.newHashMap();
            options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
                    XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
            mainResource.save(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            REENTRANT_LOCK.unlock();
        }
    }

    private XFWPackage createRootPackage(Resource resource) {
        XFWPackage xfwPackage = XFWMM_FACTORY.createXFWPackage();
        xfwPackage.setName(JXFW_MODEL_ROOT_PACKAGE);
        xfwPackage.setNsPrefix(JXFW_MODEL_ROOT_PACKAGE);
        xfwPackage.setNsURI(XFWPackage.eNS_URI_PREFIX + "/" + JXFW_MODEL_ROOT_PACKAGE);
        resource.getContents().add(xfwPackage);
        logger.debug("Root package created");
        return xfwPackage;

    }

    private XFWPackage getRootPackage(Resource resource) {
        List<XFWPackage> result = new ArrayList<>();
        EcoreUtil.getObjectsByType(resource.getContents(), XFWMMPackage.Literals.XFW_PACKAGE)
                .forEach(xfwPackage -> result.add((XFWPackage) xfwPackage));
        if ((result.size() == 1) && (result.get(0).getName().equals(JXFW_MODEL_ROOT_PACKAGE))) {
            return result.get(0);
        } else {
            throw new RuntimeException("No root package in model");
        }
    }

    /**
     * Находит или создает новый пакет по имени класса.
     * Ищет только в создаваемой модели, не в соседних.
     * Сюда должны попадать только классы из текущего модуля сборки.
     *
     * @param qualifiedName Полное имя класса, пакет для которого нужно найти или создать
     * @return Найденный или созданный пакет
     */
    public XFWPackage findOrCreatePackageByClassName(String qualifiedName) {
        final String packageName = getPackageName(qualifiedName);
        for (EPackage epackage : rootPackage.getESubpackages()) {
            if ((epackage instanceof XFWPackage) && (epackage.getName().equals(packageName))) {
                return (XFWPackage) epackage;
            }
        }
        XFWPackage xfwPackage = XFWMM_FACTORY.createXFWPackage();
        xfwPackage.setName(packageName);
        xfwPackage.setNsPrefix(packageName);
        xfwPackage.setNsURI(XFWPackage.eNS_URI_PREFIX + "/" + packageName);
        rootPackage.getESubpackages().add(xfwPackage);
        return xfwPackage;

    }
}