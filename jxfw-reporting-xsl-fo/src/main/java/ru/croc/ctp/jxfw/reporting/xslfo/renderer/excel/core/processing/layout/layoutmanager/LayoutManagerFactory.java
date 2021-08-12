package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;

/**
 * Класс-фабрика для построения нужного экземпляра LayoutManager в зависимости от условий.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class LayoutManagerFactory {

    /**
     * Создание нужного экземпляра LayoutManager в зависимости от условий.
     * @param rootArea             - Корневая область
     * @param simplePageMasterArea - Экземпляр области SimplePageMasterArea
     * @return ILayoutManager   - возвращает экземпляр LayoutManager
     */
    public static ILayoutManager getInstance(RootArea rootArea, SimplePageMasterArea simplePageMasterArea) {

        // Если количество колонок для корневой области > 1 -
        // создаем менеджер построения разметки контента путем разбивки на колонки
        if (rootArea.getColumnCount() == 1) {
            return new StandardLayoutManager(rootArea, simplePageMasterArea);
        } else if (rootArea.getColumnCount() > 1) {
            return new ColumnLayoutManager(rootArea, simplePageMasterArea);
        } else {
            return null;
        }
    }
}
