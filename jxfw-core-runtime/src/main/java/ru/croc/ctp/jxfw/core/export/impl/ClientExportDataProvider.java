package ru.croc.ctp.jxfw.core.export.impl;

import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportRow;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Поставщик данных для экспорта, основанный на готовом наборе ДТО.
 * Подходит для клиентскокго экспорта, когда все данные пришли с клиента.
 * НИкаких дополнительных манипуляций с данными не делает.
 */
public class ClientExportDataProvider implements ExportDataProvider {

    private final Iterable<DomainTo> dtos;
    private boolean done = false;

    /**
     * Конструктор.
     *
     * @param dtos набор ДТО
     */
    public ClientExportDataProvider(Iterable<DomainTo> dtos) {
        this.dtos = dtos;
    }


    @Override
    public Iterable<ExportRow> getMoreRows() {
        if (!done) {
            done = true;
            List<ExportRow> result = new ArrayList<>();
            for (DomainTo dto : dtos) {
                result.add(column -> {
                    Object value = dto.getProperty(column.getPropName());
                    return value == null ? "" : value;
                });
            }
            return result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

}
