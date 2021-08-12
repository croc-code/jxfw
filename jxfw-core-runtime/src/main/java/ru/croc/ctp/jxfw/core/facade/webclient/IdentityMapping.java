package ru.croc.ctp.jxfw.core.facade.webclient;

/**
 * Соответствие старого и нового идентификатора, если сервер создал новый id при
 * создании объекта в хранилище. Сериализуется и отправляется в WC в составе {@link StoreResultDto}
 *
 * @since 1.2
 */
public class IdentityMapping {
    private final String type;

    private final String id;

    private final String newId;

    /**
     * Конструктор.
     *
     * @param type  - simpleName типа доменного объекта
     * @param id    - оригинальный идентификатор
     * @param newId - новый идентификатор
     */
    public IdentityMapping(String type, String id, String newId) {
        this.type = type;
        this.id = id;
        this.newId = newId;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getNewId() {
        return newId;
    }
}
