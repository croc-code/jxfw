package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import com.aspose.words.Cell;
import com.aspose.words.CompositeNode;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.TextOrientation;
import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

/**
 * Класс, хранящий функции для обработки свойств элементов в параграфе.
 * Created by vsavenkov on 28.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class InlineProperties {
    
    /**
     * Получить родителя-параграфа для элемента внутри блока.
     * @param area       - Элемент дерева объектов
     * @param nodeParent - Родительская область
     * @return Parent for inline
     * @throws Exception генерируют классы com.aspose.words
     */
    @SuppressWarnings("rawtypes")
    public static Paragraph getParentForInline(GenericArea area, CompositeNode nodeParent) throws Exception {
        
        Paragraph paragraph = (Paragraph)nodeParent;
        if (paragraph == null) {
            paragraph = BlockProperties.addBlock(area, nodeParent);
        } else if (paragraph.getNextSibling() != null) {
            // текущий параграф не последний у родителя (то есть предыдущие детки оказались блок-областями)
            paragraph = addInheritedParagraph(paragraph);
        }
        /*  NOTE: до 1.1.4 был такой код:
         *  удален в связи с http://track.rnd.croc.ru/issue/RS-9
         *  т.к. получалось, что если inline содержал хотя бы одно свойство, 
         *  то для него всегда создавался параграф.
            else if (!paragraph.HasChildNodes)
                // 1я инлайн-область внутри блок-области обрабатывается по всей строгости, поэтому для нее делается
                // отдельный параграф
                BlockProperties.addBlock(oArea, nodeParent);
        */

        return paragraph;
    }

    /**
     * Добавление абзаца со свойствам родительского (для обработки inline области, вложенной в блок область).
     * @param parentParagraph - Родительский параграф
     * @return Paragraph возвращает параграф
     */
    public static Paragraph addInheritedParagraph(Paragraph parentParagraph) {
        
        // Бордюры и отступы сверху/снизу не копируем
        Assert.isNull(parentParagraph, "oParentParagraph");
        Paragraph result = BlockProperties.addNullParagraph(parentParagraph.getParentNode());
        ParagraphFormat paragraphFormat = parentParagraph.getParagraphFormat();
        ParagraphFormat resultParagraphFormat = result.getParagraphFormat();
        resultParagraphFormat.setAlignment(paragraphFormat.getAlignment());
        resultParagraphFormat.setFirstLineIndent(paragraphFormat.getFirstLineIndent());
        resultParagraphFormat.setKeepTogether(paragraphFormat.getKeepTogether());
        resultParagraphFormat.setKeepWithNext(paragraphFormat.getKeepWithNext());
        resultParagraphFormat.setLeftIndent(paragraphFormat.getLeftIndent());
        resultParagraphFormat.setRightIndent(paragraphFormat.getRightIndent());
        resultParagraphFormat.getShading().setBackgroundPatternColor(paragraphFormat.getShading()
                .getBackgroundPatternColor());
        return result;
    }

    /**
     * Если inline в ячейке и у нее выставлено свойство reference orientation, выставляем высоту строки.
     * @param nodeParent - Родитель cell aspose
     * @param area       - текущая область дерева
     * @param fontSize   - Размер шрифта
     * @param length     - Длина строки
     */
    @SuppressWarnings("rawtypes")
    public static void setHeightForParentCell(CompositeNode nodeParent, GenericArea area, double fontSize,
                                              double length) {
        if (!(nodeParent instanceof Cell)) {
            return;
        }
        Cell cell = (Cell)nodeParent;
        if (cell == null
                || !(cell.getCellFormat().getOrientation() == TextOrientation.DOWNWARD
                || cell.getCellFormat().getOrientation() == TextOrientation.UPWARD)) {
            return;
        }
        double dblHeight = length * (6.6 * fontSize) / 10.0;
        cell.getParentRow().getRowFormat().setHeight(dblHeight);
    }
}
