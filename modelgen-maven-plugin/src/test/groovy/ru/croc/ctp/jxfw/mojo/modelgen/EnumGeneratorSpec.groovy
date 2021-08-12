package ru.croc.ctp.jxfw.mojo.modelgen

import static java.io.File.separator

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths;

import spock.lang.Specification
import org.apache.maven.plugin.logging.Log

class EnumGeneratorSpec extends Specification {

    def "test enum generator"(){

        setup:        
		def log = Mock(Log)
		Path target = Paths.get("target")
		def modelFileName = "enum-model.xfwmm"
		File modelFile = new File("src${separator}test${separator}resources${separator}${modelFileName}")    
		def processorEnums = new EnumGenerator(modelFile, target, "ru.croc.test", new HashMap<>());		
		def processorModel = new ModelGenerator(new File("src${separator}test${separator}resources${separator}${modelFileName}"), new File("target${separator}test${separator}resources"), "ru.croc.test", log)
		
		when:		
		processorEnums != null
		processorEnums.generate();
		
		processorModel != null
		processorModel.process();
        
        then:
        1 * log.info(/Generated file: target${separator}test${separator}resources${separator}ru${separator}croc${separator}test${separator}datamodel${separator}datamodel.xtend/)

    }
	def "test enum flas generator"(){

		setup:
		def log = Mock(Log)
		Path target = Paths.get("target")
		def modelFileName = "enum-flags-model.xfwmm"
		File modelFile = new File("src${separator}test${separator}resources${separator}${modelFileName}")
		def processorEnums = new EnumGenerator(modelFile, target, "ru.croc.test", new HashMap<>());
		def processorModel = new ModelGenerator(new File("src${separator}test${separator}resources${separator}${modelFileName}"), new File("target${separator}test${separator}resources"), "ru.croc.test", log)

		when:
		processorEnums != null
		processorEnums.generate();

		processorModel != null
		processorModel.process();

		then:
		1 * log.info(/Generated file: target${separator}test${separator}resources${separator}ru${separator}croc${separator}test${separator}datamodel${separator}datamodel.xtend/)

	}

}