package ru.croc.ctp.jxfw.core.facade.webclient;

import java.util.HashMap;

/**
 * Информация о результатах сохранения UoW в формате Map для удобства дальнейшей
 * трансформации в JSON. Передается в webclient как результат вызова store(UoW).
 *
 * @author Nosov Alexander on 13.10.15.
 */
public class StoreResultDto extends HashMap<String, Object> {

    private static final long serialVersionUID = 4701865431629267625L;

    /**
     * Обновленные объекты в ходе сохранения UoW.
     */
    public static final String UPDATED_OBJECTS_FIELD_NAME = "updatedObjects";
    
    /**
     * Исходные объекты в пришедшие на сохранения в UoW.
     */
    public static final String ORIGINAL_OBJECTS_FIELD_NAME = "originalObjects";
    
    /**
     * Новые идентификаторы, сгенерированные в ходе сохранения UoW.
     */
    public static final String NEW_IDS_FIELD_NAME = "newIds";
    
    /**
     * Ошибки возникшие в ходе сохранения UoW.
     */
    public static final String ERROR_FIELD_NAME = "error";
    
    /**
     * Идентификаторы объектов, сгенерированные в ходе сохранения UoW.
     */
    public static final String IDS_FIELD_NAME = "ids";    

    /**
     * Конструктор.
     */
    public StoreResultDto() {
        super();
    }

}
