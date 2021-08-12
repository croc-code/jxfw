package ru.croc.ctp.jxfw.transfer.component.imp;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

/**
 * Преобразует DTO объект.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public interface ImportTransformHandler {
    /** Преобразует DTO.
     * @param domainTo преобразуемый DTO.
     * @return преобразованный DTO.
     */
    DomainTo transform(DomainTo domainTo);
}
