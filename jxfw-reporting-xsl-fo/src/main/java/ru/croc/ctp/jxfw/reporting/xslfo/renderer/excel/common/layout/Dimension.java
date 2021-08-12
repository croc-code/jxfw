package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, инкапсулирующий размерность.
 * Immutable аля string
 * Created by vsavenkov on 25.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class Dimension {
    
    /**
     * Значение размерности.
     */
    protected final float value;

    /**
     * Признак - задана ли размерность в процентах.
     */
    private boolean percentage;

    /**
     * Свойство - признак того, что размерность задана в процентах.
     * @return boolean - возвращает true, если размерность задана в процентах и false в противном случае
     */
    public boolean isPercentage() {
        return percentage;
    }

    /**
     * Свойство - значение размерности.
     * @return float    - возвращает значение размерности
     */
    public float getValue() { 
        return value;
    }

    /**
     * Свойство - установлено ли значение размерности > 0.
     * @return boolean - возвращает true, если установлено значение размерности > 0 и false в противном случае
     */
    public boolean hasValue() {
        return value > 0;
    }

    /**
     * Свойство - установлено ли хоть какое-то значение размерности.
     * @return boolean - возвращает true, если установлено значение размерности и false в противном случае
     */
    public boolean isDefined() {
        return !Float.isNaN(value);
    }

    /**
     * Конструктор по умолчанию.
     */
    protected Dimension() {
        value = Float.NaN;
    }

    /**
     * Инициализирующий конструктор.
     * @param value      - Значение размерности
     * @param percentage - Признак - задана ли размерность в процентах
     */
    protected Dimension(float value, boolean percentage) {
        
        this.value = value;
        this.percentage = percentage;
    }

    /**
     * Инициализирующий конструктор.
     * @param value - Значение размерности
     */
    protected Dimension(String value) {
        
        if (StringUtils.isBlank(value)) {
            this.value = Float.NaN;
        } else if (HelpFuncs.isPercentValue(value)) {
            // Значение задано в процентах
            this.value = HelpFuncs.getPercentValueAsFloat(value);
            // 0% == 0 (по любому)
            percentage = this.value != 0F;
        } else {
            // Значение задано в виде абсолютной величины - преобразовываем в пиксели
            Integer integerValue = HelpFuncs.getSizeInPixelsEx(value);
            this.value = null != integerValue ? integerValue.intValue() : Float.NaN;
        }
    }

    /**
     * Значение "Не определено".
     */
    public static Dimension UNDEFINED = new Dimension();

    /**
     * Значение = 0.
     */
    public static Dimension ZERO = new Dimension(0f, false);

    /**
     * Сравнение двух размерностей.
     * @param dimension - Сравниваемая размерность
     * @return boolean - возвращает true, если размерности эквивалентны и false в противном случае
     */
    public boolean isEquals(Dimension dimension) {
        
        if (null == dimension) {
            return false;
        }

        if (!(isDefined() && dimension.isDefined())) {
            return isDefined() ^ dimension.isDefined();
        }

        return dimension.getClass() == Dimension.class
                    && dimension.value == value
                    && dimension.percentage == percentage;
    }

    /**
     * Сравнение размерности с объектом, предположительно являющимся тоже размерностью.
     * @param obj - сравниваемый объект
     * @return boolean - возвращает true, если объекты эквивалентны и false в противном случае
     */
    @Override
    public boolean equals(Object obj) {
        return isEquals(obj instanceof Dimension ? (Dimension)obj : null);
    }

    /**
     * Хэш-код объекта.
     * @return int  - возвращает хэш-код объекта
     */
    @Override
    public int hashCode() {
        return !percentage ? Float.hashCode(value) : Float.hashCode(-value);
    }

    /* TODO: сдаётся мне, что это лишнее
    /// <summary>
    /// Перегруженный оператор эквивалентности
    /// </summary>
    /// <param name="oDim1"></param>
    /// <param name="oDim2"></param>
    /// <returns></returns>
    public static boolean operator ==(Dimension oDim1, Dimension oDim2)
    {
        // Если это 2 null или один и тот же объект, сразу выходим с успехом
        if (ReferenceEquals(oDim1, oDim2)) {
            return true;
        }
        // Проверяем, что 1й объект не является null
        if (null == oDim1) {
            return false;
        }
        return oDim1.isEquals(oDim2);
    }

    /// <summary>
    /// Перегруженный оператор неэквивалентности
    /// </summary>
    /// <param name="oDim1"></param>
    /// <param name="oDim2"></param>
    /// <returns></returns>
    public static boolean operator !=(Dimension oDim1, Dimension oDim2) {
        return !(oDim1 == oDim2);
    }
    */

    /**
     * Кэш.
     */
    private static ThreadLocal<Map<String, Dimension>> threadLocalScope = new ThreadLocal<>();

    /**
     * Возвращает кэш.
     * @return Map - возвращает кэш
     */
    private static final Map<String, Dimension> getCache() {
        return threadLocalScope.get();
    }

    /**
     * Инициализирует кэш.
     * @param cache - кэш
     */
    private static final void setCache(Map<String, Dimension> cache) {
        threadLocalScope.set(cache);
    }

    /**
     * Возвращает новый экземпляр объекта размерности.
     * Значение размерности не может быть задано в процентах - по умолчанию - false
     * @param value - Значение размерности в единицах
     * @return Dimension    -  возвращает новый экземпляр объекта размерности
     */
    public static Dimension getInstance(float value) {
        return new Dimension(value, false);
    }

    /**
     * Возвращает новый экземпляр объекта размерности.
     * Значение размерности задано в виде строки
     * @param value - Значение размерности в виде строки
     * @return Dimension    -  возвращает новый экземпляр объекта размерности.
     */
    public static Dimension getInstance(String value) {
        
        if (StringUtils.isBlank(value)) {
            return UNDEFINED;
        }
        if (getCache() == null) {
            setCache(new HashMap<String, Dimension>());
        }
        Dimension dimension = getCache().get(value);
        if (dimension == null) {
            dimension = new Dimension(value);
            getCache().put(value, dimension);
        }
        return dimension;
    }

    /**
     * Очистка кэша.
     */
    public static void clearCache() {
        setCache(null);
    }
}
