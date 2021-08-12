package ru.croc.ctp.jxfw.transfer.service;

import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import javax.annotation.Nonnull;

/**
 * Класс преобразования DomainTo в удобный для XFW2 формат.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@Service
public interface TransferToTransformer {

    /**
     * @param dto ТО объект по котоорому необходимо собрать XML.
     * @return строка с XML представлением данного объекта.
     */
    @Nonnull
    String toXml(@Nonnull DomainTo dto);

}
