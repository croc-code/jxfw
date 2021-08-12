package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import static com.aspose.words.NodeType.BODY;
import static com.aspose.words.NodeType.CELL;
import static com.aspose.words.NodeType.PARAGRAPH;
import static com.aspose.words.NodeType.SECTION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.SPACE_CHAR;

import com.aspose.words.Cell;
import com.aspose.words.CellFormat;
import com.aspose.words.CompositeNode;
import com.aspose.words.ConvertUtil;
import com.aspose.words.PageSetup;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Section;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.XslFoCulture;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.PageSequenceMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster.SinglePageMasterReferenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Locale;


/**
 * Вспомогательные ф-ии.
 * Created by vsavenkov on 26.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("rawtypes")
public class HelpFuncs {

    private static Logger logger = LoggerFactory.getLogger(HelpFuncs.class);

    /**
     * Нулевое значение для переменных типа double.
     */
    public static final double ZERO = 0d;

    /**
     * нельзя передавать 0 в конструктор изображения.
     * Вместо отсутствующего размера передам 1. Потом размер пересчитается.
     */
    private static final int STARTED_SIZE = 1;

    /**
     * Аналог одноименной ф-ции ORACLE. Возвращает 1й аргумент, если он не null. Иначе, второй аргумент
     *
     * @param compareValue - Сравниваемый объект
     * @param defaultValue - Объект, возвращаемый при значении NULL 1го аргумента
     * @param <T>          - Тип элемента
     * @return T    - возвращает 1й аргумент, если он не null. Иначе, второй аргумент
     */
    @SuppressWarnings("unchecked")
    public static <T> T nvl2(Object compareValue, T defaultValue) {

        return null == compareValue ? defaultValue : (T) compareValue;
    }

    /**
     * Поиск дочерней области по ее типу.
     *
     * @param parentArea - Родительская область
     * @param areaType   - Желаемый тип
     * @return IArea - возвращает желаемую область либо null
     */
    public static IArea findArea(IArea parentArea, AreaType areaType) {

        if (parentArea.getAreaType() == areaType) {
            return parentArea;
        }

        if (parentArea.isHasChildren()) {
            for (IArea currentArea : parentArea.getChildrenList()) {
                IArea area = findArea(currentArea, areaType);
                if (area != null) {
                    return area;
                }
            }
        }

        return null;
    }

    /**
     * Возвращает признак, задана ли величина в виде процента.
     *
     * @param value - Cтроковое значение величины
     * @return boolean  - возвращает признак задания величины в процентах
     */
    public static boolean isPercentValue(String value) {

        return (value.indexOf(GlobalData.PERCENT_CHAR) > 0);
    }

    /**
     * Возвращает значение величины - процент.
     *
     * @param value - Cтроковое значение величины
     * @return double   - возвращает значение величины - процента
     */
    public static double getPercentValue(String value) {

        int position = value.indexOf(GlobalData.PERCENT_CHAR);
        return position > 0 ? getSize(value.substring(0, position)) : ZERO;
    }

    /**
     * Возвращает значение величины - процент
     * метод из из Croc.XmlFramework.ReportService.ReportRenderer.Excel.Common.Utility.Utils
     *
     * @param value - Cтроковое значение величины
     * @return float    - возвращает значение величины - процента
     */
    public static float getPercentValueAsFloat(String value) {

        return getValueFromString(value.substring(0, value.indexOf(GlobalData.PERCENT_CHAR)));
    }

    /**
     * Получение пути из строки.
     *
     * @param path - Строка
     * @return String   - возвращает путь
     */
    public static String getPathString(String path) {

        int index = path.indexOf(GlobalData.URL_START);
        int urlStartLength = GlobalData.URL_START.length();
        if (index != -1) {
            path = path.substring(index + urlStartLength, path.length() - urlStartLength - index - 1);
            path = path.replace(GlobalData.APOSTROPHE, StringUtils.EMPTY);
        }
        return path;
    }

    /**
     * Возвращает значение величины - пропорциональное значение для функции proportional-column-width(value).
     *
     * @param value - Cтроковое значение величины
     * @return float    - возвращает значение величины - пропорционального значения
     */
    public static float getProportionalWidth(String value) {

        int temp1;
        int temp2;

        // Получаем значение пропорциональности ширины колонки
        temp1 = value.indexOf(GlobalData.LEFT_PARENTHESIS_CHAR);
        temp2 = value.indexOf(GlobalData.RIGHT_PARENTHESIS_CHAR);

        return getValueFromString(value.substring(temp1 + 1, temp2));
    }

    /**
     * Возвращает значение величины - значение с плавающей запятой.
     *
     * @param value - Cтроковое значение величины
     * @return float    - возвращает значение величины
     */
    public static float getValueFromString(String value) {

        Double result = parseDoubleValue(value);
        return (float) (null != result ? result : 0);
    }

    /**
     * Получение значение размера (длины, ширины ...) в пикселах.
     *
     * @param value - Значение размера - строка, например '10.0pt'
     * @return Integer  - возвращает значение размера в пикселах или null, если аргумент ошибочный
     */
    public static Integer getSizeInPixelsEx(String value) {
        logger.debug("Trying get size in pixel: " + value);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        value = value.trim(); // Обрезаем пробельные символы слева и справа
        int valueLength = value.length();
        // Коэффициент пересчета в пиксели (по умолчанию зададим невозможный коэффициент, чтобы показать, что нет
        // ед. измерения
        double convertRatio = -1;
        if (valueLength > GlobalData.MIN_SIZE_VALUE_LENGTH) {
            // Получаем наименование единицы измерения и определим коэффициент пересчета
            switch (value.substring(valueLength - GlobalData.MIN_SIZE_VALUE_LENGTH)) {
                case GlobalData.UNIT_IN:
                    convertRatio = GlobalData.IN_PIXELS_CONVERT_RATIO;
                    break;
                case GlobalData.UNIT_CM:
                    convertRatio = GlobalData.CM_PIXELS_CONVERT_RATIO;
                    break;
                case GlobalData.UNIT_MM:
                    convertRatio = GlobalData.MM_PIXELS_CONVERT_RATIO;
                    break;
                case GlobalData.UNIT_PT:
                    convertRatio = GlobalData.PT_PIXELS_CONVERT_RATIO;
                    break;
                case GlobalData.UNIT_PX:
                    convertRatio = GlobalData.PX_PIXELS_CONVERT_RATIO;
                    break;
                case GlobalData.UNIT_PC:
                    convertRatio = GlobalData.PC_PIXELS_CONVERT_RATIO;
                    break;

                default:
                    // В импортруемом коде ничего не было
                    logger.error("Cannot get size in pixel. Value of property: " + value);
            }

        }
        Double returnValue;
        if(convertRatio > 0 ) {
            returnValue = parseDoubleValue(value.substring(0, valueLength - GlobalData.MIN_SIZE_VALUE_LENGTH));
            if (returnValue!=null) {
                returnValue *= convertRatio;
            }
        } else {
            returnValue = parseDoubleValue(value);
        }

        if (returnValue == null) {
            return null;
        }
        int result = returnValue.intValue();
        // Размер не может быть отрицательным!
        return result < 0 ? null : result;
    }

    /**
     * Получение значение размера (длины, ширины ...) в пикселах.
     *
     * @param value - Значение размера - строка, например '10.0pt'
     * @return int  - возвращает значение размера в пикселах или 0, если аргумент ошибочный
     */
    public static int getSizeInPixels(String value) {

        Integer returnValue = getSizeInPixelsEx(value);
        return null != returnValue ? returnValue : 0;
    }

    /**
     * Переводит размеры XslFo в пункты.
     *
     * @param value - Строковое значение величины
     * @return double   - возвращает размер в пунктах. В случае некорректного значения возвращает 0
     */
    public static double getSizeInPoints(String value) {

        Double returnValue = getSizeInPointsEx(value);
        return null != returnValue ? returnValue : ZERO;
    }

    /**
     * Переводит размеры XslFo в пункты.
     *
     * @param value - Строковое значение величины
     * @return double   - возвращает размер в пунктах. В случае неудачи возвращает null,
     * вместо отрицательных значений - 0.
     */
    public static Double getSizeInPointsEx(String value) {

        if (StringUtils.isBlank(value)) {
            return null;
        }
        value = StringUtils
                .strip(value, String.valueOf(GlobalData.WHITE_SPACES)); // Обрезаем пробельные символы слева и справа
        int valueLength = value.length();
        Double result;
        String unitName;
        // Задана величина без единицы измерения. По умолчанию ед. измерения = пиксели
        if (valueLength <= GlobalData.MIN_SIZE_VALUE_LENGTH) {
            result = parseDoubleValue(value);
            unitName = GlobalData.UNIT_PX;
        } else {
            // Получаем наименование единицы измерения
            unitName = value.substring((valueLength - GlobalData.MIN_SIZE_VALUE_LENGTH));
            // Возвращаем значения размера в пунктах
            switch (unitName) {
                case GlobalData.UNIT_IN:
                case GlobalData.UNIT_CM:
                case GlobalData.UNIT_MM:
                case GlobalData.UNIT_PT:
                case GlobalData.UNIT_PX:
                case GlobalData.UNIT_PC:
                    result = parseDoubleValue(value.substring(0, valueLength - GlobalData.MIN_SIZE_VALUE_LENGTH));
                    break;
                default:
                    // По умолчанию ед. измерения = пиксели
                    result = parseDoubleValue(value);
                    break;
            }
        }

        // Размер не может быть отрицательным!
        if (null == result || result <= ZERO) {
            return ZERO;
        }

        switch (unitName) {
            case GlobalData.UNIT_IN:
                return ConvertUtil.inchToPoint(result);
            case GlobalData.UNIT_CM:
                // 1 см = 10 мм
                return ConvertUtil.millimeterToPoint(10d * result);
            case GlobalData.UNIT_MM:
                return ConvertUtil.millimeterToPoint(result);
            case GlobalData.UNIT_PT:
                return result;
            case GlobalData.UNIT_PC:
                // 1 px = 12 pc
                return ConvertUtil.pixelToPoint(12d * result);
            default:
                return ConvertUtil.pixelToPoint(result);
        }
    }

    /**
     * Получение размера из строки. Если строка не является числом или положительным числом, то возвращается 0.
     *
     * @param value - строка, содержащая размер
     * @return double   - возвращает размер из строки
     */
    public static double getSize(String value) {

        Double result = parseDoubleValue(value);
        return null != result && result >= ZERO ? result : ZERO;
    }

    /**
     * Перевод величины из строки в формат Double используя настройки CultureInfo.
     *
     * @param value - Строка со значением
     * @return Double   - возвращает значение Double или null, если конверсия не удалась
     */
    public static Double parseDoubleValue(String value) {

        Double returnValue;

        try {
            Locale locale = XslFoCulture.getCultureInfo();
            NumberFormat format = null == locale ? NumberFormat.getInstance() : NumberFormat.getInstance(locale);
            format.setGroupingUsed(true);
            Number number = format.parse(value);
            returnValue = number.doubleValue();

            return returnValue;
        } catch (ParseException e) {
            try {
                // попытемся использовать в качестве десятичного разделителя точку,
                // разделители тысячных недопустимы
                NumberFormat format = NumberFormat.getInstance();
                Number number = format.parse(value);
                returnValue = number.doubleValue();

                return returnValue;
            } catch (ParseException ex) {
                // если опять неудача, то верну null
                return null;
            }
        }
    }

    /**
     * Получить значение ширины от объекта родителя.
     *
     * @param node - Объект aspose
     * @return double   - возвращает размер в поинтах
     * @throws Exception - генерируют методы Aspose
     */
    public static double getWidthForFill(CompositeNode node) throws Exception {

        while (node != null) {
            switch (node.getNodeType()) {
                case CELL:
                    CellFormat format = ((Cell) node).getCellFormat();
                    double dblWidth = format.getWidth()
                            - Math.max(format.getLeftPadding(), format.getBorders().getLeft().getLineWidth() / 2)
                            - Math.max(format.getRightPadding(), format.getBorders().getRight().getLineWidth() / 2);
                    return dblWidth > ZERO ? MathUtils.round(dblWidth, 1) : ZERO;

                case SECTION:
                    PageSetup pageSetup = ((Section) node).getPageSetup();
                    if (pageSetup.getTextColumns().getCount() > 1) {
                        return pageSetup.getTextColumns().getWidth();
                    }

                    return MathUtils.round(pageSetup.getPageWidth()
                            - pageSetup.getLeftMargin() - pageSetup.getRightMargin(), 1);

                case PARAGRAPH:
                    ParagraphFormat paragraphFormat = ((Paragraph) node).getParagraphFormat();
                    return getWidthForFill(node.getParentNode())
                            - paragraphFormat.getLeftIndent() - paragraphFormat.getRightIndent();
                default:
                    node = node.getParentNode();
                    break;
            }
        }
        return ZERO;
    }

    /**
     * Получить нужного родителя, чтобы добавить объект.
     *
     * @param nodeParent - Узел, относительно которого ищется родитель, позволяющий вставлять
     * @return CompositeNode    - Объект Aspose
     */
    public static CompositeNode getNodeForBlock(CompositeNode nodeParent) {

        if (nodeParent.getNodeType() == SECTION) {
            return ((Section) nodeParent).getBody();
        }
        CompositeNode tempNode = nodeParent;
        while (nodeParent != null) {
            switch (nodeParent.getNodeType()) {

                case CELL:
                case BODY:
                    return nodeParent;

                default:
                    nodeParent = nodeParent.getParentNode();
                    break;
            }
        }
        return tempNode;
    }

    /**
     * Проверка того, что область содержит только inline-области.
     *
     * @param parentArea - Проверяемая Xsl-fo область
     * @return boolean  - возвращает true, если область содержит только inline-области и  false в противном случае
     */
    public static boolean onlyInlineAreasInside(GenericArea parentArea) {

        if (parentArea.hasChildren()) {
            for (Object current : parentArea.getChildrenList()) {
                GenericArea childArea = (GenericArea) current;
                switch (childArea.getType()) {
                    case INLINE:
                    case CHARACTER:
                    case BASIC_LINK:
                    case PAGE_NUMBER:
                    case PAGE_NUMBER_CITATION:
                        if (!onlyInlineAreasInside(childArea)) {
                            return false;
                        }
                        break;

                    default:
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Переводит ресурс файла Fo в ресурс Word.
     *
     * @param foSource - Значение атрибута src
     * @return String   - возвращает значение ресурса для aspose
     */
    public static String getImageSource(String foSource) {

        if (StringUtils.isBlank(foSource)) {
            return foSource;
        }
        StringBuilder source = new StringBuilder(StringUtils.EMPTY);
        boolean status = false;
        if (!foSource.toLowerCase().startsWith("url")) {
            return foSource;
        }

        for (char ch : foSource.toCharArray()) {
            switch (ch) {
                case '(':
                    status = true;
                    continue;
                case ')':
                    status = false;
                    continue;

                default:
                    // В импортруемом коде ничего не было
            }
            if (status) {
                source.append(ch);
            }
        }
        return source.toString();
    }

    /**
     * Получить мастер страницы из контейнера мастеров страниц по названию.
     *
     * @param layoutMasterSet - Контейнер мастеров страниц
     * @param masterReference - Имя мастера
     * @return SimplePageMasterArea - возвращает мастер страницы
     */
    public static SimplePageMasterArea getSimplePageMaster(Hashtable layoutMasterSet, String masterReference) {

        if (layoutMasterSet.containsKey(masterReference)) {
            GenericArea page = (GenericArea) layoutMasterSet.get(masterReference);
            switch (page.getType()) {
                case SIMPLE_PAGE_MASTER:
                    return (SimplePageMasterArea) page;

                case PAGE_SEQUENCE_MASTER:
                    PageSequenceMasterArea sequenceMaster = (PageSequenceMasterArea) page;
                    if (sequenceMaster.getSinglePageMasterReferences().size() > 0) {
                        SimplePageMasterArea pageMaster;
                        String masterName = (String) ((SinglePageMasterReferenceArea) sequenceMaster
                                .getSinglePageMasterReferences().get(0))
                                .getPropertyValue(FoPropertyType.MASTER_REFERENCE);
                        pageMaster = (SimplePageMasterArea) layoutMasterSet.get(masterName);
                        return pageMaster;
                    }

                    throw new RuntimeException(
                            "В теге PageSequenceMaster отсутствует элемент с именем мастер страницы");

                default:
                    // В импортруемом коде ничего не было
            }
        } else {
            throw new RuntimeException("Не найдена мастер страница");
        }
        return null;
    }

    /**
     * Удаление из строки всех пробельных символов, встречающихся более 1 подряд.
     *
     * @param value            - Исходная строка
     * @param isKeepFirstSpace - Сохранять ли начальный пробел
     * @param isKeepLastSpace  - Сохранять ли концевой пробел
     * @return String   - возвращает обработанную строку
     */
    public static String removeDoubleSpaces(String value, boolean isKeepFirstSpace, boolean isKeepLastSpace) {

        if (StringUtils.isBlank(value)) {
            return value;
        }
        int valueLength = value.length();
        char[] chResult = new char[valueLength];
        int resultCount = 0;
        boolean isRemoveSpace = !isKeepFirstSpace;
        for (int i = 0; i < valueLength; i++) {
            if (value.charAt(i) > GlobalData.SPACE_CHAR) {
                // Нормальный символ
                chResult[resultCount++] = value.charAt(i);
                isRemoveSpace = false;
            } else if (!isRemoveSpace) {
                chResult[resultCount++] = GlobalData.SPACE_CHAR; // Все пробельные символы приводим к пробелу
                isRemoveSpace = true;
            }
        }
        if (!isKeepLastSpace && resultCount > 0 && chResult[resultCount - 1] == GlobalData.SPACE_CHAR) {
            resultCount--;
        }

        return resultCount == valueLength ? value : new String(chResult, 0, resultCount);
    }

    /**
     * Конвертирование указанного кол-ва пикселей в приблизительно соотв. кол-во пробелов.
     * Используется для реализации красной строки.
     *
     * @param pixels - Отступ
     * @param font   - Шрифт области
     * @return String   - возвращает строку, содержащую кол-во пробелов, приблизительно соотв. отступу в пикселах
     */
    public static String convertPixelsToSpaces(float pixels, Font font) {

        if (pixels <= 0) {
            return StringUtils.EMPTY;
        }

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textwidth = fm.stringWidth(String.valueOf(SPACE_CHAR));
        g2d.dispose();

        return StringUtils.repeat(String.valueOf(SPACE_CHAR),
            Math.round(pixels / textwidth));
    }
}
