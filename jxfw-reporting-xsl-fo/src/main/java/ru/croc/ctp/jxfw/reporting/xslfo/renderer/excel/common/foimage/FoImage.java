package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.foimage;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

/**
 * Класс, инкапсулирующий загрузку картинок различных форматов и с различными путями.
 * абсолютный путь, относительный путь, загрузка через интернет
 * Created by vsavenkov on 24.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoImage {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(FoImage.class);
    
    /**
     * Содержит картинку.
     */
    private Image image;
    
    /**
     * Ширина картинки.
     */
    private int width;
    
    /**
     * Высота картинки.
     */
    private int height;

    /**
     * Инициирующий конструктор.
     * @param imagePath - Путь к картинке
     */
    public FoImage(String imagePath) {
        
        if (GlobalData.IMAGE_NONE.equals(imagePath)) {
            return;
        }

        imagePath = HelpFuncs.getPathString(imagePath);

        loadImage(getUriFromPath(imagePath));
    }

    /**
     * Преобразует путь к URI.
     * @param imagePath - Путь к картинке
     * @return URI  - возвращает путь в формате URI
     */
    private URI getUriFromPath(String imagePath) {
        
        URI uri = null;

        try {
            // Пытаемся получить
            uri = new URI(imagePath);
        } catch (URISyntaxException e) {
            // Пытаемся получить
            try {
                uri = new URI(Paths.get(imagePath).getFileName().toFile().getAbsolutePath());
            } catch (URISyntaxException ex) {
                logger.error("Файл не найден :" + imagePath);
            }
        }

        return uri;
    }

    /**
     * Загрузка файла по указанному URI.
     * @param uri - URI файла картинки
     */
    private void loadImage(URI uri) {
        try {
            BufferedImage bufferedImage = ImageIO.read(uri.toURL());
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
            image = bufferedImage;
        } catch (IOException e) {
            logger.error("Файл не найден :" + uri.toString());
        }
    }

    /**
     * Свойство - ширина картинки.
     * @return int  - возвращает ширину картинки
     */
    public int getWidth() {
        
        return width;
        /* TODO: если данные не верны, то надо переводить следующий код:
        return image == null || image.Width == 0
            ? 0
            : image.HorizontalResolution == 0
            ? image.Width
            : Convert.ToInt32(image.Width * GlobalData.IN_PIXELS_CONVERT_RATIO / image.HorizontalResolution);
        */
    }

    /**
     * Свойство - высота картинки.
     * @return int  - возвращает высоту картинки
     */
    public int getHeight() {

        return height;
        /* TODO: если данные не верны, то надо переводить следующий код:
        return image == null || image.Height == 0
            ? 0
            : image.VerticalResolution == 0
            ? image.Height
            : Convert.ToInt32(image.Height * GlobalData.IN_PIXELS_CONVERT_RATIO / image.VerticalResolution);
        */
    }

    /**
     * Получаем картинку.
     * @return Image    - возвращает картинку
     */
    public Image getImage() {
        return image;
    }


    /**
     * Получаем поток картинки.
     * @return Stream возвращает поток картинки
     */
    @SuppressWarnings("rawtypes")
    public Stream getStream() {
        /* TODO: Пока непонятно как это должно выглядеть в Java просто выкину исключение
        Stream oStream = new MemoryStream();
        ImageFormat oImageFormat = ImageFormat.getBmp();
        image.Save(oStream, oImageFormat);

        return oStream;
        */
        throw new NotImplementedException("TODO: Need implement!");
    }
}
