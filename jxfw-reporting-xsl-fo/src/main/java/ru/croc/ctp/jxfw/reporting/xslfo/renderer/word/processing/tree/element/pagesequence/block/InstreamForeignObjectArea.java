package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.block;

import com.aspose.words.ConvertUtil;
import de.vandermeer.svg2vector.applications.base.SvgTargets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.Svg2Image;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.XmlUtil;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:instream-foreign-object.
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class InstreamForeignObjectArea extends GenericArea {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(InstreamForeignObjectArea.class);

    /**
     * Директория картинки.
     */
    private String pictureResource;
    
    /**
     * Инициализирующий конструктор.
     * @param reader        - Ссылка на xml reader
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public InstreamForeignObjectArea(XmlTextReader reader, GenericArea parentArea,
                                     Map<String, String> attributeList) {
        super(AreaType.INSTREAM_FOREIGN_OBJECT, parentArea, attributeList);
        try {
            pictureResource = createSvg2Image(reader);
        } catch (Exception e) {
            logger.error("Error create image", e);
        }
    }

    /**
     * Свойство - Адрес картинки.
     * @return String   - возвращает адрес картинки
     */
    public String getPictureResource() {
        return pictureResource;
    }

    /**
     * Создание картинки.
     * @param reader - объект для чтения XML
     * @return String возвращает имя файла с картинкой
     * @throws Exception исключения генерятся классами, связанными с DocumentBuilder`ом
     */
    private static String createSvg2Image(XmlTextReader reader) throws Exception {
        
        // Переходим на узел <svg>
        reader.readStartElement();
        if (!reader.isEmptyElement()) {
            reader.moveToNextElement();
            if ("svg:svg".equals(reader.getPrefixedName())) {
                // Вычитываем содержимое встроенного SVG объекта
                String svgXml = reader.readOuterXml();

                // Если строка пустая, выходим
                if (svgXml.length() == 0) {
                    return null;
                }

                // Получаем значения высоты и ширины картинки
                svgXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + svgXml;
                Document document = XmlUtil.createXmlDocument(svgXml);
                Element element = document.getDocumentElement();

                // Определяем высоту и ширину
                Node widthAttr = element.getAttributes().getNamedItem(GlobalData.WIDTH_PROPERTY);
                Node heightAttr = element.getAttributes().getNamedItem(GlobalData.HEIGHT_PROPERTY);

                double width = 100;
                double height = 100;

                if (widthAttr != null) {
                    width = ConvertUtil.pointToPixel(HelpFuncs.getSizeInPoints(widthAttr.getNodeValue()));
                }

                if (heightAttr != null) {
                    height = ConvertUtil.pointToPixel(HelpFuncs.getSizeInPoints(heightAttr.getNodeValue()));
                }

                // Создаем конвертер                    
                Svg2Image svg2Image = new Svg2Image((int)width, (int)height);

                // Устанавливаем источник
                try {
                    svg2Image.setSource(svgXml);
                } catch (Exception e) {
                    throw new RuntimeException("Входящий SVG документ имеет некорректный формат, ", e);
                }

                String imageResources = svg2Image.getImageFileName(SvgTargets.wmf);

                return imageResources;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
