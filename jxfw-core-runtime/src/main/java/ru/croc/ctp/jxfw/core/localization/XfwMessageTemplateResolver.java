package ru.croc.ctp.jxfw.core.localization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Сервис осуществляет локализацию сообщений и заменяет плейсхолдеры
 * типов доменных объектов их локализованными именами из метамодели.
 * @author OKrutova
 * @since 1.6
 */
@Service
public class XfwMessageTemplateResolver {

    /**
     * бандл ресурсов.
     */
    private MessageSource messageSource;

    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Autowired
  //  @Qualifier("xfwMessageSource")
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public XfwModel getXfwModel() {
        return xfwModel;
    }

    private XfwModel xfwModel;

    @Autowired
    public void setXfwModel(XfwModel xfwModel) {
        this.xfwModel = xfwModel;
    }


    /**
     * Формирование сообщения по шаблону.
     *
     * @param messageTemplate шаблон
     * @param locale требуемая локаль
     * @return сообщение
     */
    public String resolve(XfwMessageTemplate messageTemplate, Locale locale) {
        String result;
        if (messageSource != null && messageTemplate.getBundleCode() != null
                && !messageTemplate.getBundleCode().isEmpty()) {
            result = messageSource.getMessage(
                    messageTemplate.getBundleCode(),
                    messageTemplate.getArguments(),
                    messageTemplate.getDefaultMessage(),
                    locale);
        } else {

            result = MessageFormat.format(
                    messageTemplate.getDefaultMessage() != null ? messageTemplate.getDefaultMessage() : "",
                    messageTemplate.getArguments());
        }

        if (xfwModel != null) {
            result = xfwModel.resolveMetadata(result, locale.getLanguage());
        }
        return result;
    }


}
