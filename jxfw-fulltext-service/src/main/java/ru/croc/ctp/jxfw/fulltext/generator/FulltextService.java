package ru.croc.ctp.jxfw.fulltext.generator;

import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration;

import java.util.List;

/**
 * Общий интерфейс кодогенератора, для работы с полнотекстом.
 *
 * @author SMufazzalov
 * @since 1.4
 */
public interface FulltextService {

    /**
     * Регистрация всех необходимых классов: сервисы, репозитории, сущности, компараторы и т.д.
     *
     * @param elements классы
     */
    void register(List<? extends ClassDeclaration> elements);

    /**
     * Заполнение классов необходимыми полями и аннотациями.
     *
     * @param elements классы
     */
    void transform(List<? extends MutableClassDeclaration> elements);
    /**
     * Генерация скриптов, ecore...
     *
     * @param elements классы
     */
    void generate(List<? extends TypeDeclaration> elements);
}
