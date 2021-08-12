package ru.croc.ctp.jxfw.cli.compiler;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import ru.croc.ctp.jxfw.cli.compiler.SelectedFilesCompiler;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class SelectedFilesCompilerTest {

    @Test
    public void predicate() {
        File file1 = new File("../demo-build/../demo-cass/src/main/java/ru/croc/ctp/demo/cass/domain/OutTable.xtend");
        File file2 = new File("../demo-cass/src/main/java/ru/croc/ctp/demo/cass/domain/OutTable.xtend");

        URI file2URI = URI.createFileURI(file2.getAbsolutePath());
        
        boolean test = SelectedFilesCompiler
                .INTERESTING_CHANGE_PREDICATE
                .test(Arrays.asList(file1), file2URI);
        
        assertTrue(test);
    }
}
