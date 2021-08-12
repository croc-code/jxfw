package ru.croc.ctp.jxfw.async.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Базовые настройки для работы с каналом двустороннего обмена сообщениями,
 * между клиентом и сревером.
 *
 * @author SMufazzalov
 * @since 1.4
 */
@Configuration
@EnableWebSocketMessageBroker
public class XfwAsyncConfig extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * Endpoint для хэндшейка.
     */
    public static final String HANDSHAKE_ENDPOINT = "/jxfw-ws";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(HANDSHAKE_ENDPOINT).withSockJS().setClientLibraryUrl("../client/vendor/sockjs.js");;
    }

    /**
     * Конфигурация брокеров например простой брокер в памяти {@link SimpleBrokerRegistration},
     * {@link StompBrokerRelayRegistration} подключение по TCP к другому Full-Featured брокеру который понимает
     * под-протокол STOMP.
     *
     * @param registry реестр брокеров.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //простой брокер для рассылки сообщений клиентам
        registry.enableSimpleBroker("/topic", "");
        //префикс для маршрутизация сообщений к аннотированным методам приложения
        registry.setApplicationDestinationPrefixes("/app");
    }

}
