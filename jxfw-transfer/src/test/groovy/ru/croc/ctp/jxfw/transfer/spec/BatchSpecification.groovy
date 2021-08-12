package ru.croc.ctp.jxfw.transfer.spec

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import ru.croc.ctp.jxfw.transfer.config.TestConfig
import spock.lang.Specification

/**
 * Базовый класс спецификации Spock для тестов Batch задач.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = TestConfig)
class BatchSpecification extends Specification {


}
