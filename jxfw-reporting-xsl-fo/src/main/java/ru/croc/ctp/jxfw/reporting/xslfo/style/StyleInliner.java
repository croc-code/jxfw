package ru.croc.ctp.jxfw.reporting.xslfo.style;

import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.xml.sax.SAXException;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Класс утитлитный, который умеет заинлайнить смешанные стили вида
 * class="x-report-empty" font-size="10pt" color="#FFFFFF"
 * распарсив имя класса из файла css.
 *
 * @author SMufazzalov
 * @since jXFW 1.6.0
 */
@Service
public class StyleInliner implements IStyleInliner {

    private static final Logger logger = LoggerFactory.getLogger(StyleInliner.class);

    private static final String cssFile =  "styles.css";


    @Autowired
    private XfwReportProfileManager profileManager;


    /**
     * Стиль.
     *
     * @param styleString строка со стилями class="x-report-empty" font-size="10pt" color="#FFFFFF"
     * @return заинлайненный.
     */
    @Override
    public String inlined(String styleString) {

        try(InputStream resourceAsStream = profileManager.getResource(cssFile).getInputStream()){
            List<Attr> normalized = new ArrayList<>();
            try {
                NamedNodeMap attributes = ReportStyle.getAttributes(styleString);
                for (int i = 0; i < attributes.getLength(); i++) {
                    Attr attr = (Attr) attributes.item(i);
                    String name = attr.getName();
                    if ("class".equalsIgnoreCase(name)) {
                        String fromCssFile = tryToInline(attr, resourceAsStream);
                        NamedNodeMap attributesFromCss = ReportStyle.getAttributes(fromCssFile);
                        for (int y = 0; y < attributesFromCss.getLength(); y++) {
                            normalized.add((Attr) attributesFromCss.item(y));
                        }
                    } else {
                        normalized.add(attr);
                    }
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                logger.debug("Ошибка в описании стиля = " + styleString);
            }
            return StringUtils.join(normalized, " ");

        }catch (IOException ex){
            logger.debug("CSS file not found: " + cssFile);
            return styleString;

        }

    }

    private String tryToInline(Attr attr, InputStream resourceAsStream) throws IOException {
        InputSource source = new InputSource(new InputStreamReader(resourceAsStream));
        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

        CSSRuleList cssRules = sheet.getCssRules();
        for (int i = 0; i < cssRules.getLength(); i++) {
            CSSRule item = cssRules.item(i);
            if (item instanceof CSSStyleRuleImpl) {
                if (((CSSStyleRuleImpl) item).getSelectorText().replaceAll("\\.", "").equals(attr.getValue())) {
                    List<Property> properties = ((CSSStyleDeclarationImpl) ((CSSStyleRuleImpl) item).getStyle())
                            .getProperties();
                    StringBuilder builder = new StringBuilder();
                    for (Property property : properties) {
                        builder.append(property.getName());
                        builder.append("=");
                        builder.append("\"" + property.getValue() + "\"");
                        builder.append(" ");
                    }
                    return builder.toString();
                }
            }
            System.out.println(item);
        }

        return null;
    }
}
