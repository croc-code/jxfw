package ru.croc.ctp.jxfw.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Интерфейс для WebClient.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
public interface JxfwUserDetails extends UserDetails {
    /**
     * Формирует JSON с данными пользователя, чтобы вернуть его при удачном логине.
     *
     * @return JSON объект.
     */
    String toJsonString();
}
