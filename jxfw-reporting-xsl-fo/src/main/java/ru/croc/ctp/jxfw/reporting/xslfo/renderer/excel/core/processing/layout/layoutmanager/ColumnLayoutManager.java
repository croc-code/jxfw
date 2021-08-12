package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc.CommonArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.FlowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;

import java.util.List;

/**
 * Класс, инкапсулирующий поведение построения разметки контента путем разбивки на колонки.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ColumnLayoutManager extends GenericLayoutManager {

    /**
     * Количество колонок.
     */
    private final int columnCount;

    /**
     * Расстояние между колонками.
     */
    private final int columnGap;

    /**
     * Ширина колонки.
     */
    private final int columnWidth;

    /**
     * Инициализирующий конструктор.
     * @param rootArea             - Корневая область. Ширина уже установлена.
     * @param simplePageMasterArea - Экземпляр области SimplePageMasterArea
     */
    public ColumnLayoutManager(RootArea rootArea, SimplePageMasterArea simplePageMasterArea) {
        super(simplePageMasterArea);


        columnCount = rootArea.getColumnCount();
        columnGap = rootArea.getColumnGap();
        columnWidth = rootArea.getBorderRectangle().getWidth();
    }

    /**
     * Метод для расчета расположения областей.
     * (координаты начала области по ширине и высоте - X и Y)
     * Координаты X, Y считаются в рамках листа Excel.
     * При вызове метода для родительской области координаты должны быть заданы.
     * @param parentArea - Родительская область
     */
    @Override
    protected void calculateAreaCoordinates(IArea parentArea) throws XslFoException {

        // Рассчитываем координаты только для области ROOT
        if (parentArea.getAreaType() != AreaType.ROOT) {
            return;
        }

        // Рассчитываем координаты в рамках одной колонки - получается одна колонка
        super.calculateAreaCoordinates(parentArea);

        // Определяем - есть ли области с атрибутом span
        boolean hasColumnLayoutSpanAreas = determineColumnLayoutSpanAreas((RootArea)parentArea);

        // Разбиваем одну колонку на колонки в рамках прямоугольника,
        // ограниченного областью c атрибутом span и/или страницы
        if (!hasColumnLayoutSpanAreas) {
            doCalculateAreaCoordinates(parentArea);
        } else {
            doCalculateAreaCoordinatesWithSpan(parentArea);
        }
    }

    /**
     * Определение - есть ли области с атрибутом span у корневой области.
     * @param rootArea - Корневая область
     * @return boolean  - возвращает флаг - имеются области с атрибутом span
     */
    private static boolean determineColumnLayoutSpanAreas(RootArea rootArea) {

        for (IArea pageSequence : rootArea.getChildrenList()) {
            for (IArea pageSequenceChild : pageSequence.getChildrenList()) {
                if (pageSequenceChild.getAreaType() == AreaType.FLOW) {
                    for (IArea flowChild : pageSequenceChild.getChildrenList()) {
                        if (flowChild.isColumnLayoutSpan()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Рекурсивный метод для расчета расположения областей.
     * (координаты начала области по ширине и высоте - X и Y)
     * Координаты X, Y считаются в рамках листа Excel.
     * При вызове метода для родительской области координаты должны быть заданы.
     * @param area - Текущая область
     */
    private void doCalculateAreaCoordinates(IArea area) {

        AreaRectangle borderRectangle = area.getBorderRectangle();
        int page = borderRectangle.getY() / (columnCount * pageContentHeight) + 1;
        int column = (borderRectangle.getY() - (page - 1) * columnCount * pageContentHeight) / pageContentHeight + 1;

        // Устанавливаем новые координаты области
        borderRectangle.setX(getExcelCoordinateX(borderRectangle.getX(), column));
        borderRectangle.setY(getExcelCoordinateY(borderRectangle.getY() % pageContentHeight, page));

        // Устанавливаем у всех предков признак, что имеют разбитых на колонки потомков
        int heightGap = getHeightGap(borderRectangle);
        if (heightGap > 0) {
            prepareForSplit(area);
            if (!area.isHasChildren() || area.getProgressionDirection() == AreaProgressionDirection.INLINE) {
                borderRectangle.setHeight(borderRectangle.getHeight() - heightGap);

                // Устраняем расхождение в высоте у всех областей следующих за текущей и их дочерних
                List<IArea> parentChildrenList = area.getParentArea().getChildrenList();
                int counter = 0;
                int index = parentChildrenList.indexOf(area) + 1;
                for (IArea childArea : parentChildrenList) {
                    if (counter >= index) {
                        eliminateHeightGap(childArea, heightGap);
                    }
                    counter++;
                }
            }
        }

        // Обрабатываем все дочерние области
        if (area.isHasChildren()) {
            for (IArea childArea : area.getChildrenList()) {
                doCalculateAreaCoordinates(childArea);
            }
        }
    }

    /**
     * Устранение расхождения в высоте у текущей области и всех дочерних областей.
     * @param parentArea - Родительская область
     * @param heightGap  - Различие в высоте
     */
    private static void eliminateHeightGap(IArea parentArea, int heightGap) {

        parentArea.getBorderRectangle().setY(parentArea.getBorderRectangle().getY() - heightGap);

        if (parentArea.isHasChildren()) {
            for (IArea childArea : parentArea.getChildrenList()) {
                eliminateHeightGap(childArea, heightGap);
            }
        }
    }

    /**
     * Получение превышения высоты области над высотой колонки.
     * @param borderRectangle - Прямоугольник области
     * @return int  - возвращает превышение высоты
     */
    private int getHeightGap(AreaRectangle borderRectangle) {
        return borderRectangle.getY() % pageContentHeight + borderRectangle.getHeight() - pageContentHeight;
    }

    /**
     * Подготовка области к разбиению.
     * @param area - Область
     */
    private static void prepareForSplit(IArea area) {

        // Подготавливаем исходную область
        // Устанавливаем у области и ее непосредственных предков признак наличия разбитых областей
        IArea currentArea = area;
        while (currentArea.getParentArea() != null) {
            currentArea = currentArea.getParentArea();
            currentArea.setSplitted(true);
        }

        // Устанавливаем свойства границ
        area.setPropertyValue(FoPropertyType.BORDER_TOP, area.getInheritedPropertyValue(FoPropertyType.BORDER_TOP));
        area.setPropertyValue(FoPropertyType.BORDER_BOTTOM,
                area.getInheritedPropertyValue(FoPropertyType.BORDER_BOTTOM));
        area.setPropertyValue(FoPropertyType.BORDER_LEFT, area.getInheritedPropertyValue(FoPropertyType.BORDER_LEFT));
        area.setPropertyValue(FoPropertyType.BORDER_RIGHT, area.getInheritedPropertyValue(FoPropertyType.BORDER_RIGHT));
    }

    /**
     * Метод для расчета расположения областей.
     * (координаты начала области по ширине и высоте - X и Y)
     * c учетом наличия элементов с атрибутом span
     * При вызове метода для родительской области координаты должны быть заданы.
     * @param parentArea - Родительская область
     */
    private void doCalculateAreaCoordinatesWithSpan(IArea parentArea) {

        int currentY = 0;            // Текущая позиция(Y) в рамках листа Excel
        int flowCounter = 0;
        for (IArea pageSequence : parentArea.getChildrenList()) {
            for (IArea pageSequenceChild : pageSequence.getChildrenList()) {
                // Обрабатываем области FLOW
                if (pageSequenceChild.getAreaType() == AreaType.FLOW) {
                    if (flowCounter == 0) {
                        currentY = pageSequenceChild.getBorderRectangle().getY();
                    }

                    currentY += processFlowWithSpanChildren((FlowArea)pageSequenceChild, currentY);
                    flowCounter++;
                }
            }
        }
    }

    /**
     * Обработка областей FLOW c дочерними элементами имеющими атрибут span.
     * @param flowArea - Область элемента &lt;fo:flow&gt;
     * @param currentY - Текущая позиция(Y) в рамках листа Excel
     * @return int  - возвращает результирующую высоту области в рамках листа Excel
     */
    private int processFlowWithSpanChildren(FlowArea flowArea, int currentY) {

        int resultHeight = 0;                          // Результирующая высота
        // Создаем временную область - область секции
        IArea sectionArea = new CommonArea(null, null);

        // Проходим по всем дочерним областям элемента <fo:flow>
        for (IArea flowChildArea : flowArea.getChildrenList()) {
            AreaRectangle childAreaRectangle = flowChildArea.getBorderRectangle();
            if (!flowChildArea.isColumnLayoutSpan()) {
                // Добавляем область в список областей секции
                sectionArea.getChildrenList().add(flowChildArea);
            } else {
                // Обрабатываем секцию и корректируем позицию области с атрибутом span
                if (sectionArea.isHasChildren()) {
                    int sectionHeight = processSection(sectionArea, currentY);

                    sectionArea.getChildrenList().clear();
                    resultHeight += sectionHeight;
                    currentY += sectionHeight;

                    // Устанавливаем позицию области с атрибутом span
                    fixCoordinates(flowChildArea.getChildrenList(), 0, currentY - childAreaRectangle.getY());
                    childAreaRectangle.setY(currentY);
                }

                resultHeight += childAreaRectangle.getHeight();
                currentY += childAreaRectangle.getHeight();
            }
        }
        return resultHeight;
    }

    /**
     * Обработка секции - области вывода до области с атрибутом span.
     * @param sectionArea - Временная область со списком областей секции
     * @param currentY    - Текущая позиция(Y) в рамках листа Excel
     * @return int  - возвращает высоту секции
     */
    private int processSection(IArea sectionArea, int currentY) {

        int sectionHeight = 0;                             // Высота секции

        // Вычисляем высоту секции
        for (IArea sectionChildArea : sectionArea.getChildrenList()) {
            sectionHeight += sectionChildArea.getBorderRectangle().getHeight();
        }
        sectionHeight = Math.round((float)sectionHeight / (float) columnCount);

        // Выставляем координаты и размеры
        sectionArea.setBorderRectangle(new AreaRectangle(0, currentY, columnWidth, sectionHeight));
        pageContentHeight = sectionHeight;

        // Вычисляем координаты дочерних областей секции
        IArea firstArea = sectionArea.getChildrenList().get(0);
        fixCoordinates(sectionArea.getChildrenList(), 0, -firstArea.getBorderRectangle().getY());
        doCalculateAreaCoordinates(sectionArea);
        fixCoordinates(sectionArea.getChildrenList(), 0, currentY);

        return sectionHeight;
    }

    /**
     * Исправление координат у списка областей и всех вложенных областей.
     * @param arrayList - Список областей
     * @param fixX      - Значение, на которое нужно исправить X
     * @param fixY      - Значение, на которое нужно исправить Y
     */
    private static void fixCoordinates(List<IArea> arrayList, int fixX, int fixY) {

        if (arrayList == null) {
            return;
        }
        for (IArea area : arrayList) {
            area.getBorderRectangle().setX(area.getBorderRectangle().getX() + fixX);
            area.getBorderRectangle().setY(area.getBorderRectangle().getY() + fixY);
            if (area.getChildrenList() != null) {
                fixCoordinates(area.getChildrenList(), fixX, fixY);
            }
        }
    }

    /**
     * Получение координаты X в координатной сетке листа Excel.
     * @param coordinateX   - Координата X в координатной сетке колонки страницы
     * @param currentColumn - Номер текущей колонки страницы
     * @return int  - возвращает координату X в координатной сетке листа Excel
     */
    private int getExcelCoordinateX(int coordinateX, int currentColumn) {
        return (currentColumn - 1) * (columnWidth + columnGap) + coordinateX;
    }

    /**
     * Получение координаты Y в координатной сетке листа Excel.
     * @param coordinateY - Координата Y в координатной сетке колонки страницы
     * @param currentPage - Номер текущей страницы
     * @return int  - возвращает координату Y в координатной сетке листа Excel
     */
    private int getExcelCoordinateY(int coordinateY, int currentPage) {
        return (currentPage - 1) * pageContentHeight + coordinateY;
    }
}
