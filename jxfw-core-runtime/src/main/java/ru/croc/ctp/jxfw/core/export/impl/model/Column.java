package ru.croc.ctp.jxfw.core.export.impl.model;


/**
 * POJO для описания колонки в экспорте.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class Column {
    /**
     * Наименование.
     */
    private String name;
    /**
     * Заголовок.
     */
    private String title;
    /**
     * Тип значение.
     */
    private String vt;
    /**
     * Наименование свойства, используемого для получения данных столбца.
     * Если не задано, то используется значение поля name.
     */
    private String prop;
    /**
     * Роль столбца. Поддерживаются только роли data и number.
     */
    private String role = "data";
    /**
     * Относительная ширина столбца. Реальная ширина вычисляется как:
     * {ширина страницы} * {относительная ширина столбца} / {сумма относительных ширин для всех столбцов}
     * Если хотя бы для одного столбца не задана ширина, то все столбцы выводятся одинаковой ширины.
     */
    private Integer width = 0;
    /**
     * Формат-строка.
     */
    private String format;


    private boolean hidden;

    /**
     * Стиль заголовка.
     */
    private Object headerStyle;


    /**
     * Стиль.
     */
    private Object style;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVt() {
        return vt;
    }

    public void setVt(String vt) {
        this.vt = vt;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }


    /**
     * Возвращает имя свойства для текущего столбца.
     * @return имя
     */
    public String getPropName() {
        if (prop == null || prop.isEmpty()) {
            return name;
        } else {
            return prop;
        }
    }



    public Object getHeaderStyle() {
        return headerStyle;
    }

    public void setHeaderStyle(Object headerStyle) {
        this.headerStyle = headerStyle;
    }

    public Object getStyle() {
        return style;
    }

    public void setStyle(Object style) {
        this.style = style;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
