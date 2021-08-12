package ru.croc.ctp.jxfw.cli.compiler;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler;
import org.eclipse.xtext.mwe.NameBasedFilter;
import org.eclipse.xtext.mwe.PathTraverser;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Компилятор xtend файлов.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
public class SelectedFilesCompiler extends XtendBatchCompiler {
    @Inject
    private IEncodingProvider.Runtime encodingProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectedFilesCompiler.class);

    private Collection<File> changedFiles;
    public static final BiPredicate<Collection<File>, URI> INTERESTING_CHANGE_PREDICATE = (changedFiles, input) -> {
        File file = new File(input.toFileString());
        boolean matches = changedFiles
                .stream()
                .map(File::getAbsolutePath)
                .filter(changed ->
                        Paths.get(changed).normalize().compareTo(Paths.get(file.getAbsolutePath()).normalize()) == 0
                )
                .findAny()
                .isPresent();
        return matches;
    };

    public void setChangedFiles(Collection<File> changedFiles) {
        this.changedFiles = changedFiles;
    }

    /**
     * Ради этого метода, который вернет нужные, лишь те xtend в которых произошли какие то изменения, вся петрушка.
     *
     * @param resourceSet менеджер ресурсов, необходимых для компиляции
     * @return отфильтрованный менеджер
     */
    @Override
    public ResourceSet loadXtendFiles(ResourceSet resourceSet) {
        encodingProvider.setDefaultEncoding(getFileEncoding());
        final NameBasedFilter nameBasedFilter = new NameBasedFilter();
        nameBasedFilter.setExtension(fileExtensionProvider.getPrimaryFileExtension());
        PathTraverser pathTraverser = new PathTraverser();
        List<String> sourcePathDirectories = getSourcePathDirectories();

        Multimap<String, URI> pathes = pathTraverser.resolvePathes(sourcePathDirectories, new Predicate<URI>() {
            @Override
            public boolean apply(URI input) {
                return INTERESTING_CHANGE_PREDICATE.test(changedFiles, input);
            }
        });
        for (String src : pathes.keySet()) {
            for (URI uri : pathes.get(src)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("load xtend file '" + uri + "'");
                }
                resourceSet.getResource(uri, true);
            }
        }
        return resourceSet;
    }

}
