package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.collection;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс инкапсулирующий работу с массивом целых чисел.
 * Created by vsavenkov on 24.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@Deprecated()
public class IntArray {
    
    /**
     * Начальное значение по умолчанию.
     */
    private static final int DEFAULT_INITIAL_VALUE = Integer.MIN_VALUE;

    /**
     * Массив целых чисел.
     */
    private int[] integerArray;

    /**
     * Свойство - размер массива.
     * @return int  - возвращает размер массива
     */
    public int getSize() { 
        return integerArray.length;
    }

    /**
     * Инициализирующий конструктор.
     * @param size - Размер массива
     * @throws XslFoException - генерирует исключение, если передан отрицательный размер массива
     */
    public IntArray(int size) throws XslFoException {
        
        if (size < 0) {
            throw new XslFoException("Задан неправильный размер массива:" + size, "IntArray");
        }

        integerArray = new int[size];

        // Инициализируем элементы массива значением по умолчанию
        for (int i = 0; i < size; i++) {
            integerArray[i] = DEFAULT_INITIAL_VALUE;
        }
    }

    /**
     * Метод установки элементов списка.
     * @param values - Список, должен быть проинициализирован
     * @throws XslFoException - генерирует, если передан null
     */
    public void setValues(List<Integer> values) throws XslFoException {
        
        if (values == null) {
            throw new XslFoException("Список не задан", "IntArray.setValues");
        }

        integerArray = values.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Установка значения элемента массива.
     * @param index - Индекс элемента
     * @param value - Значение элемента
     * @throws XslFoException - генерирует, если переданный индекс превышает размеры массива
     */
    public void setValue(int index, int value) throws XslFoException {
        
        if (index >= integerArray.length) {
            throw new XslFoException("Задан неправильный индекс элемента массива:" + index, "IntArray.setValue");
        }

        integerArray[index] = value;
    }

    /**
     * Получение значения элемента массива.
     * @param index - Индекс элемента
     * @return int- возвращает значение элемента массива
     * @throws XslFoException - генерирует, если переданный индекс превышает размеры массива
     */
    public int getValue(int index) throws XslFoException {
        
        if (index >= integerArray.length) {
            throw new XslFoException("Задан неправильный индекс элемента массива:" + index, "IntArray.getValue");
        }

        return integerArray[index];
    }

    /**
     * Оставить в массиве только уникальные значения, начальное значение не включается.
     */
    public void makeDistinctValues() {
        
        if (integerArray.length == 0) {
            return;
        }

        // Сортируем массив по возрастанию
        Arrays.sort(integerArray);

        // Обеспечиваем корректность работы цикла - начальные данные
        int previousValue = integerArray[0] + 1;

        // Проходим по всем элементам массива
        List<Integer> arrayList = new ArrayList<>();    // Вспомогательный список
        for (int i = 0; i < integerArray.length; i++) {
            int currentValue = integerArray[i];                            // Текущее значение элемента массива
            if (currentValue != DEFAULT_INITIAL_VALUE && integerArray[i] != previousValue) {
                arrayList.add(currentValue);
            }
            previousValue = currentValue;
        }

        integerArray = arrayList.stream().mapToInt(value -> value).toArray();
    }

    /**
     * Возвращает индекс последнего элемента значение которого равняется искомому.
     * Если элемент не найден - возвращает -1
     * Бинарный поиск. Массив должен быть отcортирован по возрастанию.
     * @param value - Искомое значение элемента
     * @return int  - возвращает индекс элемента значение которого равняется искомому
     */
    public int indexOfByBinarySearch(int value) {
        int rangeBeginIndex = 0;
        int rangeEndIndex = integerArray.length - 1;
        int index;
        while (rangeBeginIndex < rangeEndIndex) {
            index = rangeBeginIndex + (rangeEndIndex - rangeBeginIndex) / 2;

            int currentValue = integerArray[index];
            if (currentValue == value) {
                return index;
            } 
            if (value > currentValue) {
                rangeBeginIndex = index + 1;
            } else {
                rangeEndIndex = index - 1;
            }
        }

        index = rangeBeginIndex + (rangeEndIndex - rangeBeginIndex) / 2;
        if (value == integerArray[index]) {
            return index;
        }
        return -1;
    }
}
