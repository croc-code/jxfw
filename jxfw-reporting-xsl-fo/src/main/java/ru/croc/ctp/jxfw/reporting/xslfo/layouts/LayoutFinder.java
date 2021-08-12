package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.LayoutsClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import javax.xml.bind.JAXBElement;

/**
 * Класс содержит функции поиска лэйаута.
 * Created by vsavenkov on 04.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0 (reportClass)
 */
public class LayoutFinder {

    /**
     * Поиск лэйаута по наименованию.
     * @param layouts       - класс-представитель списка лэйаутов
     * @param layoutName    - имя лэйаута
     * @return AbstractLayoutClass  - представитель лэйаута или null (если не найден)
     */
    private static AbstractLayoutClass findLayout(LayoutsClass layouts, String layoutName) {

        if (layouts == null || layouts.getAbstractLayout() == null) {
            return null;
        }

        for (JAXBElement<? extends AbstractLayoutClass> layoutNode : layouts.getAbstractLayout()) {
            AbstractLayoutClass layout = layoutNode.getValue();
            if (layout.getN().equals(layoutName)) {
                return layout;
            }
        }

        return null;
    }

    /**
     * Поиск лэйаута по наименованию.
     * @param reportClass   - Десериализованный объект профиля отчета
     * @param layoutName    - Наименование лэйаута
     * @return AbstractLayoutClass  - представитель лэйаута или null (если не найден)
     */
    public static AbstractLayoutClass findLayout(ReportClass reportClass, String layoutName) {

        AbstractLayoutClass layout = null;

        for (JAXBElement<? extends AbstractLayoutClass> layoutTypeNode : reportClass.getLayouts().getAbstractLayout()) {
            Object layoutType = layoutTypeNode.getValue();
            if (LayoutsClass.class.equals(layoutType.getClass())) {
                layout = findLayout((LayoutsClass)layoutType, layoutName);
            } else if (ReportClass.PageSequenceClass.class.equals(layoutType.getClass())) {
                ReportClass.PageSequenceClass pageSequence = (ReportClass.PageSequenceClass) layoutType;
                if (pageSequence.getPageHeader() != null) {
                    layout = findLayout(pageSequence.getPageHeader().getLayouts(), layoutName);
                }

                if (layout == null && pageSequence.getPageBody()  != null) {
                    layout = findLayout(pageSequence.getPageBody().getLayouts(), layoutName);
                }

                if (layout == null && pageSequence.getPageFooter() != null) {
                    layout = findLayout(pageSequence.getPageFooter().getLayouts(), layoutName);
                }
            }

            if (layout != null) {
                break;
            }
        }

        return layout;
    }
}
