package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.List;

/**
 * Контейнер для парсинга json формата Xfw.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTransferData {

    private List<DomainTo> objects;

    public List<DomainTo> getObjects() {
        return objects;
    }

    public void setObjects(List<DomainTo> objects) {
        this.objects = objects;
    }
}
