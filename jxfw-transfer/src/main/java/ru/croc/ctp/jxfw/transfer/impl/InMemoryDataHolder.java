package ru.croc.ctp.jxfw.transfer.impl;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.DataHolder;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Компонент временного хранения объектов для агрегации результата выполнения
 * нескольких шагов задачи.
 * 
 * @author AKogun
 * @since 1.4
 *
 * @deprecated version 1.5 replaced by component {@link ImportGroupReader}.
 */
@Component
@JobScope
public class InMemoryDataHolder implements DataHolder {

    private List<DomainTo> items = new ArrayList<>();
    private int currentPosition = 0;

    @Override
    public void write(List<? extends DomainTo> items) throws Exception {
        this.items.addAll(items);
    }

    @Override
    public DomainTo read() throws Exception {
        DomainTo result = (currentPosition < items.size()) ? items.get(currentPosition) : null;
        currentPosition++;
        return result;
    }
}
