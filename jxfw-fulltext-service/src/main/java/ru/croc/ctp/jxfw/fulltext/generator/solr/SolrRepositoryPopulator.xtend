package ru.croc.ctp.jxfw.fulltext.generator.solr

import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import javax.annotation.Nonnull
import org.springframework.data.solr.repository.SolrCrudRepository
import ru.croc.ctp.jxfw.solr.repo.SolrQueryExecutorRepository
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration

import static extension ru.croc.ctp.jxfw.fulltext.generator.solr.GeneratorHelperSolr.*

/**
 * @author SMufazzalov
 * @since 1.4
 */
class SolrRepositoryPopulator {

    val extension TransformationContext ctx

    new(@Nonnull TransformationContext context) {
        ctx = context
    }

    public def populate(MutableClassDeclaration clazz, boolean dual) {
        if(isTransient(clazz)) {
            return;
        }

        val repoInterface = ctx.findInterface(clazz.getRepositoryQName(dual))
        //TODO ругаться если не найден
        val repositoryExtendedInterfaces = #[
            SolrCrudRepository.newTypeReference(clazz.newTypeReference, clazz.getKeyType(ctx)),
            SolrQueryExecutorRepository.newTypeReference(clazz.newTypeReference, clazz.getKeyType(ctx))
        ]
        // Если репозиторий был определен разработчиком, то не нужно его генерировать
        if(repoInterface instanceof MutableInterfaceDeclaration) {
            val rep = repoInterface as MutableInterfaceDeclaration
            rep.extendedInterfaces = repositoryExtendedInterfaces
        }
    }
}