package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.collection.IntArray;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;

/**
 * Интерфейс, описывающий общее поведение построения разметки контента.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public interface ILayoutManager {

    /**
     * Метод построения разметки контента корневой области.
     * @param rootArea               - корневая область
     * @param arrHorizontalCoordList - Массив координат по горизонтали
     * @param arrVerticalCoordList   - Массив координат по вертикали
     * @throws XslFoException генерирует исключение, если структура профиля отчёта не соответствует ожиданиям
     */
    @SuppressWarnings("deprecation")
    void doLayout(RootArea rootArea, IntArray arrHorizontalCoordList, IntArray arrVerticalCoordList)
            throws XslFoException;
}
