package ru.croc.ctp.jxfw.cli.provider;

import com.google.inject.Injector;

import ru.croc.ctp.jxfw.cli.compiler.SelectedFilesCompiler;

import org.eclipse.xtend.core.XtendStandaloneSetup;

/**
 * Провайдер компилятора xtend файлов {@link SelectedFilesCompiler}.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
public class XtendCompilerProvider {

    /**
     * Получить компилятор xtend.
     *
     * @return {@link SelectedFilesCompiler}
     */
    public SelectedFilesCompiler provide() {
        Injector injector = new XtendStandaloneSetup().createInjectorAndDoEMFRegistration();
        SelectedFilesCompiler instance = injector.getInstance(SelectedFilesCompiler.class);
        return instance;
    }

}
