package ru.croc.ctp.jxfw.security.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

/**
 * Авто-конфигурация для Ldap аутентификации.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
@Configuration
@ComponentScan("ru.croc.ctp.jxfw.security")
@ConditionalOnClass(ActiveDirectoryLdapAuthenticationProvider.class)
public class LdapAutoConfiguration {

    @Value("${jxfw-security.ldap.domain:croc.ru}")
    private String domain;

    @Value("${jxfw-security.ldap.url:ldap://acdc.croc.ru:389}")
    private String url;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * @return Encoder паролей в системе.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Добавление LDAP аутентификации к провайдерам Spring Security.
     *
     * @param auth - {@link AuthenticationManagerBuilder} билдр менеджера аутентификаций.
     * @throws Exception выбрасывается любое исключение.
     */
    @Autowired
    protected void globalConfigure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).userDetailsService(userDetailsService);
    }

    /**
     * @return Spring Bean - провайдер для LDAP Active Directory аутентификации.
     */
    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider authenticationProvider =
                new ActiveDirectoryLdapAuthenticationProvider(domain, url);
        authenticationProvider.setConvertSubErrorCodesToExceptions(true);
        authenticationProvider.setUseAuthenticationRequestCredentials(true);
        return authenticationProvider;
    }
}
