package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * используется только для Solr модуля (в дизайн-тайме), 
 * если во время кодо-генерации встретится данная аннотация XFWSolrDocument, то ее заменяем на  
 * SolrDocument где значение solrCoreName выставим как keySpaceAlias + "." + solrCoreName.
 * 
 * <p>прим: в keySpaceAlias подставится значение из файла keyspace.properties (если объявить) 
 * @author SMufazzalov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface XFWSolrDocument {

    /**
     * ядро в Solr.
     * 
     * @return имя ядра в Solr
     */
    String solrCoreName();

    /**
     * keyspace (похоже на <em>схема</em> в oracle).
     * 
     * @return keyspace
     */
    String keySpaceAlias();
    
}
