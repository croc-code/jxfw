package ru.croc.ctp.jxfw.transfer.impl.exp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportDomainToProcessor;
import ru.croc.ctp.jxfw.transfer.domain.DomainToServicesResolverTransfer;
import ru.croc.ctp.jxfw.transfer.impl.BinaryPropertyService;

/**
 * Компонент для преобразования доменных объектов в соответствующие TO объекты.
 * 
 * @author AKogun
 * @since 1.4
 */
@Component
public class DefaultExportDomainToProcessor implements ExportDomainToProcessor {

    @Autowired
    private DomainToServicesResolverTransfer domainToServicesResolverTransfer;
    @Autowired
    private BinaryPropertyService binaryPropertyService;

    @SuppressWarnings("unchecked")
    @Override
    public DomainTo process(DomainObject<?> domainObject) throws Exception {
        final DomainTo dto = domainToServicesResolverTransfer
                .resolveToService(domainObject.getTypeName()).toTo(domainObject);
        binaryPropertyService.transformBinaryPropertiesToBase64(dto);
        return dto;
    }

}