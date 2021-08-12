package ru.croc.ctp.jxfw.reporting.xslfo.style;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Класс реализует коллекцию стилей.
 * Created by vsavenkov on 14.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ReportStylesCollection {

    /**
     * Хранилище стилей.
     */
    private Dictionary<String, ReportStyle> styles;

    public Dictionary<String, ReportStyle> getStyles() {
        return styles;
    }

    /**
     * Конструктор коллекции стилей.
     */
    public ReportStylesCollection() {
        styles = new Hashtable<>();
    }

    /**
     * Закрытый конструктор копии коллекции стилей отчета.
     * @param styleDictionary  - Хранилище стилей, копия которого будет создана
     */
    private ReportStylesCollection(Dictionary<String, ReportStyle> styleDictionary) {
        if (styleDictionary == null) {
            throw new ArgumentNullException("styleDictionary");
        }
        styles = new Hashtable<>(styleDictionary.size());
        while (styleDictionary.keys().hasMoreElements()) {
            String key = styleDictionary.keys().nextElement();
            styles.put(key, styleDictionary.get(key));
        }
    }

    /**
     * Метод возвращает копию текущей коллекции стилей отчета.
     * @return ReportStylesCollection - Копия текущей коллекции стилей
     */
    public ReportStylesCollection getCopy() {
        return new ReportStylesCollection(styles);
    }

    /**
     * Метод добавляет стиль.
     * @param name     - имя стиля, добавляемого в коллекцию
     * @param attrs    - атрибуты стиля
     */
    public void add(String name, String attrs) {
        if (StringUtils.isBlank(name)) {
            throw new ArgumentNullException("name");
        }
        styles.put(name, new ReportStyle(name, attrs));
    }

    /**
     * Метод возвращает копию объекта стиля коллекции с заданным именем.
     * @param name     - Имя стиля, который требуется получить. Либо сама строка атрибутов!
     * @return ReportStyle  - Копия стиля, находящегося в коллекции
     */
    public ReportStyle getReportStyle(String name) {
        return getReportStyle(name, true);
    }

    /**
     * Метод возвращает стиль с заданным именем.
     * @param name      - Имя стиля, который требуется получить. Либо сама строка атрибутов!
     * @param isClone   - Возвратить копию объекта стиля
     * @return ReportStyle - Стиль, находящийся в коллекции, либо его копия
     */
    public ReportStyle getReportStyle(String name, boolean isClone) {
        if (name == null) {
            return null;
        }
        // Получить сам стиль
        ReportStyle style = styles.get(name);

        // Если стиль не найден, то может быть вместо наименования нам саму строку атрибутов подсунули?
        if (style == null && name != null && name.indexOf('=') >= 0) {
            style = new ReportStyle(name, name);
            styles.put(name, style);
        }

        // Если получен null - в любом случае вернуть null
        if (style == null) {
            return null;
        }
        // В зависимости от параметра возвратить либо сам стиль, либо его копию
        return isClone ? new ReportStyle(style) : style;
    }
}
