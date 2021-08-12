package ru.croc.ctp.jxfw.metamodel.impl;

import static org.eclipse.emf.common.util.URI.createFileURI;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWModel;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwLocalizable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by OKrutova on 18.07.2017.
 */
public class XFWModelImpl implements XFWModel {

    private static final Logger logger = LoggerFactory.getLogger(XFWModelImpl.class);


    /**
     * EMF ресурсы.
     */
    protected final ResourceSet resourceSet;

    /**
     * Создание EMF ресурсов и регистрицаия JXFW пакета для Ecore модели.
     */
    protected XFWModelImpl() {
        resourceSet = new ResourceSetImpl();
        EPackage.Registry.INSTANCE.put(XFWMMPackage.eNS_URI, XFWMMPackage.eINSTANCE);
        Map<String, Object> extMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
        /*  extMap.put("xfwmm", new EcoreResourceFactoryImpl());
        extMap.put("ecore", new XMIResourceFactoryImpl());*/
        extMap.put("*", new XMIResourceFactoryImpl());

    }

    /**
     * Конструктор загружает метамодель по набору URI.
     *
     * @param uris идентификаторы ресурсов.
     */
    public XFWModelImpl(java.net.URI... uris) {
        this();
        for (int i = 0; i < uris.length; i++) {
            logger.debug("Load model from URI {} ", uris[i]);
            resourceSet.getResource(org.eclipse.emf.common.util.URI.createURI(uris[i].toString()), true);
        }
    }


    /**
     * Загружает модель по данному uri.
     *
     * @param uri - идентификатор модели
     */
    public XFWModelImpl(URI uri) {
        this();
        resourceSet.getResource(uri, true);

    }


    /**
     * Загружает модель из данного пути. Если передан путь к файлу, то загружаетс модель из него.
     * Если передан путь к директории, то модели  ищутся в стандарном месте расположения моделей
     * внутри исходных кодов проекта, а переданная директория рассматривается
     * как директория исходных кодов проекта.
     *
     * @param path - пути к файлу модели или к папке исходных кодов проекта
     * @see XFWMMPackage#getModelFolderPath()
     */
    public XFWModelImpl(Path path) {
        this();

        if (!path.toFile().exists()) {
            logger.warn("Path {} doesn't exist", path);
            return;
        }
        if (path.toFile().isDirectory()) {
            logger.debug("Path {} is a directory", path);
            path = path.resolve(XFWMMPackage.eINSTANCE.getModelFolderPath());
            loadModelsFromDir(path);
        } else {
            loadModel(path);
        }
    }

    /**
     * Загружает все модели из папки.
     *
     * @param path - папка.
     */
    protected void loadModelsFromDir(Path path) {
        logger.debug("Load models from {} ", path);
        if (!path.toFile().exists()) {
            logger.warn("Directory {} doesn't exist", path);
        } else {
            try (final DirectoryStream<Path> stream
                         = Files.newDirectoryStream(path, "*.{"
                    + XFWMMPackage.eINSTANCE.getModelFileExtension() + "}")) {
                for (Path p : stream) {
                    loadModel(p);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Загружает модель из данного пути.
     *
     * @param path - пути к  модели
     */
    protected void loadModel(Path path) {

        if (!path.toFile().exists() || !path.toFile().isFile()) {
            throw new RuntimeException("Model " + path.toString() + " was not found");
        }
        logger.debug("Found file: " + path.toString());
        if (path.toString().toLowerCase().endsWith(XFWMMPackage.eINSTANCE.getModelFileExtension())
                || path.toString().toLowerCase().endsWith(".xfwmm")) {
            resourceSet.getResource(createFileURI(path.toAbsolutePath().toString()), true);
            logger.debug("Model loaded {}", path);
        }

    }


    /**
     * Список пакетов, в которых находятс доменные объекты модели.
     * Один пакет может быть в нескольких моделях. Т.е. пакетов с одинаковым именем м.б.
     * несколько и классы разложены по ним в засисмости от модуля, в котором объявлены.
     * Следует искать классы, а не пакеты.
     * В прикладном коде лучше вообще не пользоваться понятием пакета.
     * Поэтому метода нет в интерфейсе.
     *
     * @return - набор пакетов
     */
    public Set<XFWPackage> getPackages() {

        Set<XFWPackage> result = new HashSet<>();
        for (Resource resource : resourceSet.getResources()) {
            for (Object xfwPackage : EcoreUtil.getObjectsByType(resource.getContents(),
                    XFWMMPackage.Literals.XFW_PACKAGE)) {
                result.add((XFWPackage) xfwPackage);
                for (EPackage subPackage : ((XFWPackage) xfwPackage).getESubpackages()) {
                    if (subPackage instanceof XFWPackage) {
                        result.add((XFWPackage) subPackage);
                    }
                }

            }
        }

        return result;
    }


    @Override
    public <T extends EClassifier> T findBySimpleName(String name, Class<T> type) {
        return find(name, type);
    }


    /* Один пакет может находиться в нескольких ресурсах.
       Поэтому пока ищем все равно полным перебором
      */
    @Override
    public <T extends EClassifier> T findByFqName(String name, Class<T> type) {
        /* final String packageName = getPackageName(name);
        XFWPackage xfwPackage = findPackage(packageName);
        if (xfwPackage != null) {
            return xfwPackage.find(name, type);
        }
        return null;*/
        return find(name, type);
    }

    @Override
    public <T extends EClassifier> T find(String name, Class<T> type) {


        for (XFWPackage xfwPackage : getPackages()) {
            T result = xfwPackage.find(name, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }


    @Override
    public <T extends EClassifier> Set<T> getAll(Class<T> type) {
        Set<T> result = new HashSet<T>();
        for (XFWPackage xfwPackage : getPackages()) {
            result.addAll(xfwPackage.getAll(type));
        }
        return result;
    }


    @Override
    public Set<String> getAvailableLanguages() {
        Set<String> availableLanguages = new HashSet<>();
        for (EClassifier eclassifier : getAll(EClassifier.class)) {
            XfwLocalizable localizable = (XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE
                    .adapt(eclassifier, XfwLocalizable.class);
            if (localizable != null) {
                availableLanguages.addAll(localizable.getAvailableLanguages());
            }
        }
        return availableLanguages;
    }


}
