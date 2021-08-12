package ru.croc.ctp.jxfw.wc.web.bootloader;

import org.springframework.core.io.Resource;
import ru.croc.ctp.jxfw.wc.web.mvc.MainPageModel;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Интерфейс bootloader'a, компонента, управляющего загрузкой страниц, скриптов, AppCache.
 *
 * @since 1.0
 */
public interface IBootloader {
    /**
     * @return Признак поддержки AppCache.
     */
    Boolean isSupportAppCache();

    /**
     * @return Относительный путь (от папки приложения) скрипта require.js.
     */
    RequirejsConfig getRequirejsConfig();

    /**
     * Возвращает список скриптов, возвращаемых одним объединенным потоком в зависимости
     * от конфигурации и параметров текущего клиента.
     *
     * @param model   - модель для главной страницы
     * @param request - объект http запроса
     * @return список скриптов, возвращаемых одним объединенным потоком.
     */
    List<String> getScripts(MainPageModel model, HttpServletRequest request);

    /**
     * Возвращает список скриптов,
     * которые надо поместить на страницу при первоначальной загрузке.
     *
     * @return Список имен файлов или null
     */
    Resource[] getBootScripts();
}
