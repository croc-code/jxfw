package ru.croc.ctp.jxfw.core.reporting.facade.webclient;

/**
 * Объект, который должен быть особым образом сериализован в json.
 * @author OKrutova
 * @since 1.6
 */
public interface ItemWithWcSpecificSerialization {

    /**
     *  Это значение становится именем свойства при сериализации,
     *  а сам объект   - значением свойства.
     * @return имя свойства.
     */
    String getName();
}
