package ru.croc.ctp.jxfw.security;

import org.springframework.security.ldap.userdetails.LdapUserDetails;

/**
 * Интерфейс расширяющий {@link org.springframework.security.ldap.userdetails.LdapUserDetails}
 * методами нужными для WebClient.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
public interface JxfwLdapUserDetails extends LdapUserDetails, JxfwUserDetails {
}
