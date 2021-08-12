package ru.croc.ctp.jxfw.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import ru.croc.ctp.jxfw.config.TestConfigFull
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = TestConfigFull)
class FileApiControllerSpec extends Specification {

    @Autowired
    ResourceStore fileStore

    def "user folder created"() {
        given:
        def login = "username@email.style"
        def auth = new UsernamePasswordAuthenticationToken(login, null)
        def securityContext = new SecurityContextImpl(authentication: auth)
        System.setProperty("spring.security.strategy", "MODE_GLOBAL")
        def holder = new SecurityContextHolder()
        holder.setStrategyName("MODE_GLOBAL")
        holder.setContext(securityContext)

        when:
        def dir = fileStore.getUserInboundDir()

        then:
        dir.absolutePath.contains(login)
    }

}