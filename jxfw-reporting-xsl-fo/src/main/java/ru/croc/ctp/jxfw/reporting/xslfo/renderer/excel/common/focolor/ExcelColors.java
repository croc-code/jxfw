package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.focolor;

import com.aspose.cells.Workbook;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, содержащий палитру Excel, занимающийся основной обработкой цветов.
 * Created by vsavenkov on 24.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ExcelColors {

    /**
     * Объект Excel.
     */
    private final Workbook excel;
    
    /**
     * индекс цвета по-умолчанию.
     */
    private static final int DEFAULT_COLOR_INDEX = 39;
    
    /**
     * индекс.
     */
    private int index;
    
    /**
     * спсиок цветов.
     */
    private final List<Integer> colors;


    /**
     * Конструктор.
     * @param excel - Объект Excel
     */
    public ExcelColors(Workbook excel) {
        
        colors = new ArrayList<>();
        this.excel = excel;
        index = DEFAULT_COLOR_INDEX;

        for (int i = 0; i < 40; i++) {
            // Создаем таблицу первых 40 основных цветов
            if (!colors.contains(this.excel.getColors()[i].toArgb())) {
                colors.add(this.excel.getColors()[i].toArgb());
            }
        }
    }

    /**
     * Функция определения или подбора цвета.
     * @param color - Определяемый цвет
     * @return Color    - возвращает цвет из палитры - уже существующий или подобранный
     */
    public Color getColor(Color color) {
        
        // Если данный цвет содержится в таблице
        if (colors.contains(color.getRGB())) {
            return color;
        }
        // Если цвета нет
        // Если еще можно разместить новые цвета в палитре
        if (index < 55) {
            // Добавляем цвет в таблицу
            colors.add(color.getRGB());
            // Добавляем цвет в палитру Excel
            excel.changePalette(com.aspose.cells.Color.fromArgb(color.getRGB()), ++index);
            // Возвращаем новый цвет
            return color;
        }
        // Иначе возвращаем "ближайший" цвет из палитры
        return getNearestColor(color);
    }

    /**
     * Подбор цвета из палитры - получение "ближайшего цвета".
     * @param color - Цвет, который нужно подобрать
     * @return Color    - возвращает подобранный цвет
     */
    public Color getNearestColor(Color color) {
        
        Color paletteColor;                        // Цвет палитры
        int minDifference = Integer.MAX_VALUE;     // Минимальное различие
        Color minDifferenceColor = Color.BLACK;    // Минимальная разность цветов

        // Анализируем отличия заданного цвета от каждого из цветов палитры
        for (int currentColor : colors) {
            // Рассчитываем разность цветов
            paletteColor = new Color(currentColor);
            // Разность составляющих цветов Red
            int redDifference = Math.abs(paletteColor.getRed() - color.getRed());
            // Разность составляющих цветов Green
            int greenDifference = Math.abs(paletteColor.getGreen() - color.getGreen());
            // Разность составляющих цветов Blue
            int blueDifference = Math.abs(paletteColor.getBlue() - color.getBlue());

            int temp = 30 * redDifference * redDifference
                       + 59 * greenDifference * greenDifference
                       + 11 * blueDifference * blueDifference;

            if (temp < minDifference) {
                minDifference = temp;
                minDifferenceColor = paletteColor;
            }
        }

        return minDifferenceColor;
    }
}
