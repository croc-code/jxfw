package ru.croc.ctp.jxfw.facade.datasource

import ru.croc.ctp.jxfw.facade.WebClientDataSourceCreator
import ru.croc.ctp.jxfw.metamodel.XFWDataSource
import ru.croc.ctp.jxfw.metamodel.XFWModel
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl
import spock.lang.Specification

import java.nio.file.Paths

import static java.io.File.separator


/**
 * Created by SMufazzalov on 14.12.2016.
 */
class DataSourceGenerationSpec extends Specification {

    /**
     * JXFW-579 Неверная генерация типа данных в DataSourcesController
     */
    def "listTypeParamPresent"() {
        given:
        XFWModel xfwModel = new XFWModelImpl(Paths.get("src${separator}test${separator}resources${separator}" +
                "models${separator}datasource${separator}JXFW-579-XFWModel.ecore"))

        def dsList =  xfwModel.getAll(XFWDataSource.class)
        def options = ["generateExport": false, "includes": [], "excludes": []]

        def dataSourceCreator = new WebClientDataSourceCreator();

        when:
        def files = dataSourceCreator.create("DS", dsList, options)

        then:
        print(files)
        files.first().toString().contains("List listStrings")
        files.first().toString().contains("Set setStrings")

    }

 }
