package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.catchword;

import static com.aspose.cells.TextAlignmentType.LEFT;
import static com.aspose.cells.TextAlignmentType.RIGHT;
import static com.aspose.cells.TextFontAlignType.CENTER;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text.InlineArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.font.FoFont;

/**
 * Класс, инкапсулирующий колонтитул.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class Catchword {

    /**
     * номер секции колонтитула.
     */
    private CatchwordSection section = CatchwordSection.UNDEFINED;
    
    /**
     * Текст секции колонтитула.
     */
    private final StringBuilder script;
    
    /**
     * Признак необходимости вставки символа перевода строки.
     */
    private boolean isLineFeed;
    
    /**
     * Текущий шрифт.
     */
    private String font;

    /**
     * Область, по которой строится колонтитул.
     * @param area - область, по которой строится колонтитул
     */
    public Catchword(IArea area) {
        script = new StringBuilder();
        doExecute(area);
    }

    /**
     * Рекурсивный обход области и её дочерних и построение текста колонтитула.
     * @param area - Область
     */
    private void doExecute(IArea area) {
        
        // Пока текста нет, пытаемся определиться с секцией, к которой будем относить футер
        if (script.length() == 0) {
            Object alignment = area.getProperty(FoPropertyType.TEXT_ALIGN);
            if (alignment != null) {
                section = getCatchwordSection(Integer.parseInt(alignment.toString()));
            }
        }

        InlineArea inlineArea = area instanceof InlineArea ? (InlineArea)area : null;

        if (inlineArea != null && !StringUtils.isBlank(inlineArea.getText())) {
            if (script.length() > 0 && isLineFeed) {
                script.append(GlobalData.LINE_FEED_CHAR);
            }

            isLineFeed = false;
            String font = generateFontScript((FoFont)area.getInheritedPropertyValue(FoPropertyType.FONT));
            if (!this.font.equals(font)) {
                script.append(font);
                this.font = font;
            }

            if (inlineArea.getAreaType() == AreaType.PAGE_NUMBER) {
                script.append("&P");
            } else {
                // Экранируем амперсанды удвоением
                script.append(inlineArea.getText().replace("&", "&&"));
            }
        } else if (area.getAreaType() == AreaType.BLOCK) {
            isLineFeed = true;
        }

        if (area.isHasChildren()) {
            // Обходим поддерево дочерних областей
            for (IArea childArea : area.getChildrenList()) {
                doExecute(childArea);
            }
        }

        if (area.getAreaType() == AreaType.BLOCK) {
            isLineFeed = true;
        }
    }

    /**
     * Свойство - секция колонтитула.
     * @return CatchwordSection   - возвращает секцию колонтитула.
     */
    public CatchwordSection getSection() {
        return section;
    }

    /**
     * Свойство - текст колонтитула.
     * @return String   - возвращает текст колонтитула
     */
    public String getScript() {
        return script.toString();
    }

    /**
     * Определение расположения колонтитула.
     * @param textAlignmentType - Значение выравнивания
     * @return CatchwordSection - возвращает значение расположения колонтитула
     */
    public static CatchwordSection getCatchwordSection(int textAlignmentType) {
        
        // Определили секцию колонтитула
        switch (textAlignmentType) {
            case LEFT:
                return CatchwordSection.LEFT;
            case CENTER:
                return CatchwordSection.CENTER;
            case RIGHT:
                return CatchwordSection.RIGHT;
            default:
                // По умолчанию левый колонтитул
                return CatchwordSection.LEFT;
        }
    }


    /**
     * Создание скрипта для шрифта, вспомогательный метод для обработки колонтитулов.
     * @param font - Объект шрифта
     * @return String - возвращает результитующий скрипт
     */
    public static String generateFontScript(FoFont font) {
        
        if (font == null) {
            font = FoFont.getDefaultFont();
        }
        String script;
        String style = ",";

        // Определяем стиль текста
        if (font.isBold()) {
            style += "Bold ";
        }
        if (font.isItalic()) {
            style += "Italic";
        }
        if (!style.equals(",")) {
            script = "&\"" + font.getName() + style + "\"";
        } else {
            script = "&\"" + font.getName() + "\"";
        }

        // Определяем размер шрифта
        script += " &" + String.format(GlobalData.getCultureInfo(), "%f", font.getSize());

        return script;
    }
}
