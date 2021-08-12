package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.croc.ctp.jxfw.transfer.impl.imp.xml.exception.BreakParsingException;

/**
 * Хэндлер для получения значения имени сценария.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class ScenarioNameHandler extends DefaultHandler {
    private String scenarioName;
    
    @Override
    public void startElement(String uri, String localName,
                             String tageName, Attributes attributes) throws SAXException {
        if ("transfer-data".equalsIgnoreCase(tageName)) {
            scenarioName = attributes.getValue("n");
        } else {
            // анти-паттерн: управление контроллем исполнения при помощи исключений.
            // не удалось найти другой более-менее правильный путь не читать весь XML файл до конца, 
            // это важно так как размер файла может быть очень большим, 
            // а название сценария обычно располагается в начале файла. 
            throw new BreakParsingException();
        }
    }

    public String getScenarioName() {
        return scenarioName;
    }
}
