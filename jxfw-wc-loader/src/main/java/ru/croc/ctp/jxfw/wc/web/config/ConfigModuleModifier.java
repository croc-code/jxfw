package ru.croc.ctp.jxfw.wc.web.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Модификатор для {@link XConfig}.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public interface ConfigModuleModifier {

    /**
     * @param config - передаваемый по конвейеру созданный конфиг 
     * @return Создание конфигурационного файла приложения.
     */
    XConfig createClientConfig(XConfig config);

}
