package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;

/**
 * Класс, инкапсулирующий стандартное поведение построения разметки контента.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class StandardLayoutManager extends GenericLayoutManager {

    /**
     * Конструктор.
     * @param rootArea             - Корневая область. Ширина уже установлена.
     * @param simplePageMasterArea - Экземпляр области SimplePageMasterArea
     */
    public StandardLayoutManager(RootArea rootArea, SimplePageMasterArea simplePageMasterArea) {
        super(simplePageMasterArea);
    }
}
