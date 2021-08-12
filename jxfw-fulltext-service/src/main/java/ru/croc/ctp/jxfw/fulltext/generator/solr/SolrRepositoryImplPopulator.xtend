package ru.croc.ctp.jxfw.fulltext.generator.solr

import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.springframework.data.solr.core.query.Query
import javax.annotation.Nonnull
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import ru.croc.ctp.jxfw.solr.facade.webclient.CriteriaComposer
import ru.croc.ctp.jxfw.solr.repo.SolrQueryExecutorRepository

import static extension ru.croc.ctp.jxfw.fulltext.generator.solr.GeneratorHelperSolr.*
import ru.croc.ctp.jxfw.solr.config.support.XfwSolrRepositoryFactoryBean
import java.util.List
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.solr.core.SolrOperations

/**
 * @author SMufazzalov
 * @since 1.4
 */
class SolrRepositoryImplPopulator {

    val extension TransformationContext ctx

    new(@Nonnull TransformationContext context) {
        ctx = context
    }

    public def populate(MutableClassDeclaration clazz, boolean dual) {

        val repositoryImpl = ctx.findClass(clazz.getRepositoryImplQName(dual))
        //TODO exception if notfound

        //TODO Если реализация репозитория была определена разработчиком, то не нужно её генерировать

        repositoryImpl.implementedInterfaces = #[
            SolrQueryExecutorRepository.newTypeReference(clazz.newTypeReference, String.newTypeReference)
        ]

        //добавить поле solrOperations
        repositoryImpl.addSolrOperationsField

        //напр UserRepository.class
        var domainRepository = clazz.getRepositoryQName(dual)

        //добавить метод setRepositories назначение которог, проинжектить доступные репозитории и определить из них
        //необходимый solrOperations
        repositoryImpl.addSetRepositoriesMethod(domainRepository)

        //имя ядра solr
        val solrCore = clazz.getSolrCore

        //методы findAll
        repositoryImpl.addFindAllMethod(clazz, solrCore)
        repositoryImpl.findAllPageableMethod(clazz, solrCore)

        //метод удалить/посчитать по запросу Query
        repositoryImpl.addDeleteByQueryMethod(solrCore)
        repositoryImpl.addCountMethod(solrCore)

    }

    private def addSolrOperationsField(MutableClassDeclaration repositoryImpl) {
        repositoryImpl.addField("solrOperations") [
            visibility = Visibility::PRIVATE
            type = SolrOperations.newTypeReference
        ]
    }

    private def addFindAllMethod (
            MutableClassDeclaration repositoryImpl,
            MutableClassDeclaration clazz,
            String solrCore
    ) {
        val findAll = repositoryImpl.addMethod('findAll') [
            visibility = Visibility::PUBLIC
            addParameter('query', Query.newTypeReference)
            returnType = Page.newTypeReference(clazz.newTypeReference)
            body = [
                '''
                if (query.getRows() == null) {
                    query.setRows(«toJavaCode(Math.newTypeReference)».toIntExact(count(query)));
                    }
                    return solrOperations.queryForPage(
                        "«solrCore»",
                        query,
                        «toJavaCode(clazz.newTypeReference)».class
                    );
                    '''
            ]
        ]

        findAll.addAnnotation(Override.newAnnotationReference)
    }

    private def findAllPageableMethod (
            MutableClassDeclaration repositoryImpl,
            MutableClassDeclaration clazz,
            String solrCore
    ) {
        val findAllPageable = repositoryImpl.addMethod('findAll') [
            visibility = Visibility::PUBLIC
            addParameter('query', Query.newTypeReference)
            addParameter('pageable', Pageable.newTypeReference)
            returnType = Page.newTypeReference(
                    clazz.
                    newTypeReference)
            body = ['''
                query.setPageRequest(«toJavaCode(CriteriaComposer.newTypeReference)».modifyColumnNames(
                    pageable, «clazz.newTypeReference».class));
                return findAll(query);
                '''
            ]
        ]

        findAllPageable.addAnnotation(Override.newAnnotationReference)
    }

    private def addDeleteByQueryMethod (
            MutableClassDeclaration repositoryImpl,
            String solrCore
    ) {
        val deleteByQuery = repositoryImpl.addMethod('delete') [
            visibility = Visibility::PUBLIC
            addParameter('query', Query.newTypeReference)
            body = ['''solrOperations.delete("«solrCore»", query);''']
        ]

        deleteByQuery.addAnnotation(Override.newAnnotationReference)
    }

    private def addCountMethod (
            MutableClassDeclaration repositoryImpl,
            String solrCore
    ) {
        val count = repositoryImpl.addMethod('count') [
            visibility = Visibility::PUBLIC
            addParameter('query', Query.newTypeReference)
            returnType = primitiveLong
            body = [
                '''return solrOperations.count("«solrCore»", query);'''
            ]
        ]

        count.addAnnotation(Override.newAnnotationReference)
    }

    private def addSetRepositoriesMethod(MutableClassDeclaration iRepositoryImpl, String domainRepository) {

        var setReposMtd = iRepositoryImpl.addMethod("setRepositories")[
            visibility = Visibility::PUBLIC
            addParameter(
                    'xfwSolrRepositoryFactoryBeans',
                    List.newTypeReference(XfwSolrRepositoryFactoryBean.newTypeReference)
            )
            body = ['''
            for (XfwSolrRepositoryFactoryBean repo : xfwSolrRepositoryFactoryBeans) {
               Class<?> repositoryInterface = repo.getRepositoryInformation().getRepositoryInterface();
               if (repositoryInterface.getCanonicalName().toLowerCase().equals("«domainRepository.toLowerCase»")) {
                  this.solrOperations = repo.solrTemplate;
                  break;
               }
            }
            ''']
        ]

        setReposMtd.addAnnotation(Autowired.newAnnotationReference)
    }
}