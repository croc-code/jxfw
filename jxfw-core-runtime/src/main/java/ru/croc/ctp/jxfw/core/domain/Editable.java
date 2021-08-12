package ru.croc.ctp.jxfw.core.domain;

/**
 * Created by SMufazzalov on 25.12.2015.
 * <p/>
 * классы доменных объектов, полученные из xtend модели и 
 * помеченные аннотацией XFWObject у которой поле isReadonly == false
 * должны наследовать этот интерфейс
 */
public interface Editable {

    /**
     * @return версия объекта.
     */
    Long getVersion();

    /**
     * @param ts - новая версия объекта.
     */
    void setVersion(Long ts);

    /**
     * @return название поля версии в модели.
     */
    String getNameOfVersionField();
}
