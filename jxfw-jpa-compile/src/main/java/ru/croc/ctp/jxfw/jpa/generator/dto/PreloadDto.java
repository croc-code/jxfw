package ru.croc.ctp.jxfw.jpa.generator.dto;

import com.querydsl.jpa.impl.JPAQuery;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO класс для переадачи данных в метод загрузки навигируемых свойств.
 * {@link ru.croc.ctp.jxfw.jpa.domain.DomainJpaService#computePreload(JPAQuery, List, int)}
 */
public class PreloadDto {
    private String typeName;
    private List<List<String>> preloads;
    private List<? extends DomainObject<? extends Serializable>> domainObjects;
    private JPAQuery<? extends DomainObject<? extends Serializable>> query;

    /**
     * Инициализируем список прелоадов.
     */
    public PreloadDto() {
        preloads = new ArrayList<>();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<List<String>> getPreloads() {
        return preloads;
    }

    public void setPreloads(List<List<String>> preloads) {
        this.preloads = preloads;
    }

    public List<? extends DomainObject<? extends Serializable>> getDomainObjects() {
        return domainObjects;
    }

    public void setDomainObjects(List<? extends DomainObject<? extends Serializable>> domainObjects) {
        this.domainObjects = domainObjects;
    }

    public void setQuery(JPAQuery<? extends DomainObject<? extends Serializable>> cloneQuery) {
        query = cloneQuery;
    }

    public JPAQuery<? extends DomainObject<? extends Serializable>> getQuery() {
        return query;
    }
}
