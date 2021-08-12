package ru.croc.ctp.jxfw.solr.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import ru.croc.ctp.jxfw.solr.config.support.XfwSolrRepositoriesRegistrar;
import ru.croc.ctp.jxfw.solr.config.support.XfwSolrRepositoryFactoryBean;

import javax.validation.OverridesAttribute;
import java.lang.annotation.*;


/**
 * Расширили EnableSolrRepositories нужными для проекты значениями (поддержка
 * нескольких ядер Solr, и завод репозитариев), чтобы избавить прикладных
 * разработчиков от нужды самим их описывать.
 *
 * @author SMufazzalov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableSolrRepositories
@Import(XfwSolrRepositoriesRegistrar.class)
public @interface XFWEnableSolrRepositories {

    /**
     * Конфигурация поддержки многоядерности.
     *
     * @return true/false
     */
    @OverridesAttribute(constraint = EnableSolrRepositories.class, name = "multicoreSupport")
    boolean multicoreSupport() default true;

    /**
     * Завол репозитариев.
     *
     * @return {@link FactoryBean}
     */
    @OverridesAttribute(constraint = EnableSolrRepositories.class, name = "repositoryFactoryBeanClass")
    Class<?> repositoryFactoryBeanClass() default XfwSolrRepositoryFactoryBean.class;

    /**
     * Базовые пакеты для сканирования классов с аннотациями.
     *
     * @return легальные имена пакетов
     */
    @OverridesAttribute(constraint = EnableSolrRepositories.class, name = "basePackages")
    String[] basePackages() default {};

    /**
     * Классы представляющие базовые пакеты для сканирования классов с аннотациями.
     *
     * @return классы
     */
    @OverridesAttribute(constraint = EnableSolrRepositories.class, name = "basePackageClasses")
    Class<?>[] basePackageClasses() default {};

}
