package ru.croc.ctp.jxfw.transfer.component.imp.context;

/**
 * Разбивает доменные объекты в контексте импорта на независимые группы,
 * которые можно загружать по отдельности.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportContextSplitterAndAggregator {
    /** Разбивает доменные объекты в контексте импорта на независимые группы,
     *  которые можно загружать по отдельности. Результат формируетс в контексте импорта.
     *  @param importContext контекст импорта, в котором будет происходить разбиение.
     */
    void split(ImportContext importContext);

    /** Объединяет маленькие группы в большие. Результат формируетс в контексте импорта.
     *  @param importContext контекст импорта, в котором будет происходить объединение.
     */
    void aggregate(ImportContext importContext);

}
