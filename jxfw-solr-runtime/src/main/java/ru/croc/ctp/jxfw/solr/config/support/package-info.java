/**
 * В Spring-data-solr версии 2.1.1-release. Отсутствует возможность при использовании multicoresupport == true
 * задать конфигурацию SolrTemplate (базовых solr операций) в части расширения QueryParsers (конвертер
 *{@link org.springframework.data.solr.core.query.SolrDataQuery} в {@link org.apache.solr.client.solrj.SolrQuery}).
 * Весь доступ к полученным SolrTemplate' ам закрыт
 * (будь он открыт то есть api
 * {@link org.springframework.data.solr.core.SolrTemplate#registerQueryParser(java.lang.Class,
 * org.springframework.data.solr.core.QueryParser)}).
 *
 * https://jira.spring.io/browse/DATASOLR-368 Register query parser with multicoreSupport
 *
 * @author SMufazzalov
 * @since 1.5
 */
package ru.croc.ctp.jxfw.solr.config.support;