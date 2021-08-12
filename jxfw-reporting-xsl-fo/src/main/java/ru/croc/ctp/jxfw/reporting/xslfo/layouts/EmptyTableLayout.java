package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.RowCellBuilder;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FoRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyLayoutClass;

import javax.xml.stream.XMLStreamException;

/**
 * Лэйаут для вывода промежутка между другими лйэаутами.
 * Рисует невидимую табличку. Реализует следующую функциональность:
 * 1. Возможность управления высотой разделителя
 * 2. Возможность подключения форматтеров к разделителю (для управления цветом и т.д.)
 * 3. Возможность добавления дополнительныого fo:block xsl-fo-тега внутри разделителя
 * Пример профиля:
 * <r:empty-layout
 *      r:n="empty"
 *      r:t="Разделитель"
 *      r:table-style-class="TABLE_CLASS"
 *      r:cell-style-class="CELL_CLASS">
 *      <r:add-xslfo>
 *          <fo:block>
 *              некий текст
 *          </fo:block>
 *      </r:add-xslfo>
 * </r:empty-layout>
 * Created by vsavenkov on 24.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class EmptyTableLayout extends ReportAbstractLayout {

    private static final Logger logger = LoggerFactory.getLogger(FoRenderer.class);

    @Override
    protected void doMake(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) {

        // наименование пустого класса
        final String emptyClassName = "EMPTY";

        EmptyLayoutClass profile = (EmptyLayoutClass) layoutProfile;

        // имя класса для таблицы
        String tableClass = StringUtils.defaultString(profile.getTableStyleClass(), emptyClassName);

        // имя класса для ячейки
        String cellClass = StringUtils.defaultIfEmpty(profile.getCellStyleClass(),tableClass);

        // строка кастомного XSLFO
        String xslfo = "&#160;";
        if (profile.getAddXslfo() != null) {
            String innerXml = profile.getAddXslfo();
            if (innerXml != null && innerXml.length() != 0) {
                xslfo = MacroProcessor.process(innerXml, layoutData);
            }
        }

        try {
            if (tableClass.length() == 0) {
                // если указан пустой стиль для таблицы, то вместо таблицы выводим простой fo:block
                layoutData.getRepGen().rawOutput(xslfo, cellClass);
            } else {
                // строим таблицу
                layoutData.getRepGen().tableStart(false, tableClass, false);
                layoutData.getRepGen().tableAddColumn();
                layoutData.getRepGen().tableRowStart();
                layoutData.getRepGen().tableRowAddCell(RowCellBuilder.create(xslfo)
                    .setElementClass(cellClass));
                layoutData.getRepGen().tableRowEnd();
                layoutData.getRepGen().tableEnd();
            }
        } catch (XMLStreamException e) {
            logger.warn("ошибка при добавлении промежутка между лэйаутами", e);
        }
    }
}
