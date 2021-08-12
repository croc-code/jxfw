package ru.croc.ctp.jxfw.facade.jpa

import ru.croc.ctp.jxfw.facade.ToServiceCreator
import ru.croc.ctp.jxfw.facade.WebClientToServiceCreator
import ru.croc.ctp.jxfw.metamodel.XFWClass
import ru.croc.ctp.jxfw.metamodel.XFWModel
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Paths

import static java.io.File.separator

/**
 *
 * @author Nosov Alexander
 *         on 03.02.16.
 */
class TOServiceGeneratorJPASpec extends Specification {

    def "test correct generate TO service (issue TERRA-208)"() {
        given:
        XFWModel xfwModel = new XFWModelImpl(Paths.get("src${separator}test${separator}resources${separator}models${separator}normal-models"
                + "${separator}model${separator}TERRA-208-MasterDetail.ecore"))
        def clazz = xfwModel.findBySimpleName("Group", XFWClass.class)


        final ToServiceCreator serviceTOCreator = new WebClientToServiceCreator()

        when:
        def files = serviceTOCreator.create(clazz, xfwModel.getAll(XFWClass.class), null)

        then:
        files.first().toString()
                .contains("Set<UserList> users = v instanceof String ? factory.createSet(Collections.singletonList((String) v), \"UserList\") : factory.createSet((List<String>) v, \"UserList\")")
    }

    @Ignore // TODO JXFW-704
    def "test correct generate TO service (issue JXFW-704)"() {
        given:

        XFWModel xfwModel = new XFWModelImpl(Paths.get("src${separator}test${separator}resources${separator}models"
                + "${separator}XFWModelMasterDetail.ecore"))
        def clazz = xfwModel.findBySimpleName("Detail", XFWClass.class)



        final ToServiceCreator serviceTOCreator = new WebClientToServiceCreator()

        when:
        def files = serviceTOCreator.create(clazz, xfwModel.getAll(XFWClass.class), null)

        then:
        true
        files.first().toString()
                .contains("master.getDetails().add(o)")


        files.first().toString()
                .contains("master.getDetails().remove(detail)")

    }


    def "в  TO service-а для абстрактоного класса подавлены fromTo и createNew"(){
        given:
        XFWModel xfwModel = new XFWModelImpl(Paths.get("src${separator}test${separator}resources${separator}models"
                + "${separator}XFWModelMasterDetail.ecore"))
        def clazz = xfwModel.findBySimpleName("AbstractMaster", XFWClass.class)

        final ToServiceCreator serviceTOCreator = new WebClientToServiceCreator()

        when:
        def files = serviceTOCreator.create(clazz, xfwModel.getAll(XFWClass.class), null)

        then:
        files.first().toString()
                .contains('''
  public AbstractMaster fromTo(@Nonnull AbstractMaster o, @Nonnull DomainTo dto, @Nonnull ConvertContext context) {
    throw new UnsupportedOperationException("Abstract Type ToService");
  }''')
        files.first().toString()
                .contains('''
  public AbstractMaster createNewDomainObject(String key) {
    throw new UnsupportedOperationException("Abstract Type ToService");
  }''')


    }



}
