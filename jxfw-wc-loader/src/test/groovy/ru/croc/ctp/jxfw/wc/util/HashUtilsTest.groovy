package ru.croc.ctp.jxfw.wc.util

import spock.lang.Specification

import java.nio.charset.Charset

/**
 * Тест для утилиты хеширования
 *
 * @author Nosov Alexander
 *         on 29.06.15.
 */
class HashUtilsTest extends Specification {

	def "test computeHash with Default Style"() {
		given:
		def input = "one_elem:two_elem"

		when:
		def hash = HashUtils.computeHash(input.getBytes(Charset.forName("UTF-8")), HashUtils.HashStyle.Default)
		
		then:
		"yaIXq/4ELsnFFiJrTku8cw==" == hash
	}
	
	def "test computeHash with Minified Style"() {
		given:
		def input = "one_elem:two_elem"

		when:
		def hash = HashUtils.computeHash(input.getBytes(Charset.forName("UTF-8")))
		
		then:
		"yaIXq/4ELsnFFiJrTku8cw" == hash
	}
}
