package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import com.aspose.words.DocumentBase;
import com.aspose.words.Row;

/**
 * Вспомогательные методы работы с таблицами.
 * Created by vsavenkov on 26.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableHelper {

    /**
     * Фабричный метод создания Row таблицы.
     * Используется для создания Row без границ (то как было до версии Aspose 10.5).
     * Начиная с 10.5 Row по умолчанию имеет границы (а следовательно и все ячейки).
     * (см. http://www.aspose.com/docs/display/wordsnet/How+to++Migrate+to+Aspose.Words+10.5+or+Higher)
     * @param wordDocument - документ
     * @return Row возвращает созданную строку таблицы
     */
    @SuppressWarnings("rawtypes")
    public static Row ceateRow(DocumentBase wordDocument) {

        Row row = new Row(wordDocument);
        row.getRowFormat().getBorders().clearFormatting();

        return row;
    }
}
