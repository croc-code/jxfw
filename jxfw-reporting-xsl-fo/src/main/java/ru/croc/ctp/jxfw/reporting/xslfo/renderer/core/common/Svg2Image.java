package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import de.vandermeer.svg2vector.applications.base.SvgTargets;
import de.vandermeer.svg2vector.applications.is.Svg2Vector_IS;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс, инкапсулирующий конвертацию из формата SVG в растровый формат.
 * Используется сторонний компонент (https://xmlgraphics.apache.org/batik/javadoc/)
 * Created by vsavenkov on 14.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class Svg2Image {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(Svg2Image.class);

    /**
     * Временная директория по умолчанию.
     */
    public static final String DEFAULT_TEMP_DIRECTORY = getTempDirectory();

    /**
     * объекты для проведения конвертирования.
     */
    private TranscoderInput input;

    //region  временные файлы
    /**
     * имя SVG файла по-умолчанию.
     */
    private String defaultSvgFileName;

    /**
     * имя результирующего файла по-умолчанию.
     */
    private String defaultImageFileName;
    //endregion

    /**
     * Полный путь до исходного файла картинки.
     */
    private String inputSvgFileName;

    /**
     * Частный конструктор.
     *
     * @param width  - Ширина картинки
     * @param height - Высота картинки
     */
    public Svg2Image(int width, int height) {

        // Установили пути для входной и выходной картинок
        defaultSvgFileName = Paths.get(DEFAULT_TEMP_DIRECTORY, getTempFileName()).toFile().getAbsolutePath();
        defaultImageFileName = Paths.get(DEFAULT_TEMP_DIRECTORY, getTempFileName()).toFile().getAbsolutePath();
        /* TODO: не понял куда запихать размеры
            Panel oPanel = new Panel();
            oPanel.Width = width;
            oPanel.Height = height;
        */
    }

    /**
     * Получаем временное уникальное имя файла.
     *
     * @return String   - возвращает временное уникальное имя файла
     */
    private String getTempFileName() {

        String tempFilename = null;
        try {
            tempFilename = File.createTempFile("", ".tmp", null).getAbsolutePath();
        } catch (IOException e) {
            logger.error("Error create temp file", e);
        }

        return tempFilename;
    }

    /**
     * Устанавливает источник данных.
     *
     * @param src - Данные элемента fo:instream-foreign-object
     */
    public void setSource(String src) {

        // Создали временный файл
        //Get the file reference
        Path path = Paths.get(defaultSvgFileName);
        //Use try-with-resource to get auto-closeable writer instance
        // Закрыли файл
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Записали туда данные
            writer.write(src);
        } catch (IOException e) {
            logger.error("Error write source to file:" + path.toFile().getAbsolutePath(), e);
        }

        //Step -1: We read the input SVG document into Transcoder Input
        //We use Java NIO for this purpose
        inputSvgFileName = null;
        try {
            inputSvgFileName = Paths.get(defaultSvgFileName).toUri().toURL().toString();
        } catch (MalformedURLException e) {
            logger.error("Error get svg file name:" + defaultSvgFileName, e);
        }
        // TODO: похоже можно записывать InputStream вместо запихивания во временный файл
        input = new TranscoderInput(inputSvgFileName);
    }

    /**
     * Возвращаем имя файла картинки, предварительно её перекодировав.
     *
     * @param imageFormat - формат результирующего изображения
     * @return String   - Путь к картинке
     */
    public String getImageFileName(SvgTargets imageFormat) {

        switch (imageFormat) {
            case wmf:
                // Пример использования и набор малопонятных параметров содран с
                // "https://github.com/vdmeer/svg2vector#using-applications-in-java"
                /* TODO: при наличии проблем разобраться с используемыми классами, т.к. на странице модуля упоминаются
                    два типа:
                    Currently supported are:
                        s2v-hp - converting SVG graphics to vector formats using Apache Batik and Freehep libraries
                        s2v-is - converting SVG graphics to vector (and selected bitmap) formats using an existing
                                    Inkscape executable
                */
                final String[] stdOptions = new String[]{
                        "--create-directories", "--overwrite-existing",
                        "--all-layers",
                        "-q",
                        "-g",
                        "-m"
                };
                Svg2Vector_IS app = new Svg2Vector_IS();
                String[] args = ArrayUtils.addAll(stdOptions,
                        "-t", imageFormat.name(),
                        "-f", inputSvgFileName,
                        "-d", defaultImageFileName
                );
                // если код возврата отличен от нуля, то логирую и выкидываю исключение
                if (0 != app.executeApplication(args)) {
                    String message = "Error transform SVG to " + imageFormat.name();
                    logger.error(message);
                    throw new RuntimeException(message);
                }
                break;

            case png:
                // по-умолчанию трансформирую в PNG найденным ранее способом, хотя у используемого для wmf модуля
                // есть своя реализация. Если встанет вопрос корректности или производительности, то сначала можно
                // попробовать её.
                try {
                    //Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
                    OutputStream outputStream = new FileOutputStream(defaultImageFileName);
                    TranscoderOutput output = new TranscoderOutput(outputStream);
                    // Step-3: Create PNGTranscoder and define hints if required
                    PNGTranscoder converter = new PNGTranscoder();
                    // Step-4: Convert and Write output
                    // Получили картинку
                    converter.transcode(input, output);
                    // Step 5- close / flush Output Stream
                    outputStream.flush();
                    outputStream.close();
                } catch (TranscoderException | IOException e) {
                    logger.error("Error convert svg", e);
                }
                break;

            default:
                throw new NotImplementedException("Don`t implement transform SVG to " + imageFormat.name());
        }

        // Возвращаем имя файла
        return defaultImageFileName;
    }

    /**
     * Получение/создание временного каталога для хранения файлов.
     *
     * @return String   - возвращает путь временного каталога для хранения файлов
     */
    private static String getTempDirectory() {

        // Если каталог уже существует, то пытаемся удалить его вместе с содержимым
        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "XslFOTmp");
        // Если каталог уже существует, то пытаемся удалить его вместе с содержимым
        try {
            Files.deleteIfExists(tempPath);
        } catch (IOException e) {
            logger.error("Error delete temp directory: " + tempPath.toFile().getAbsolutePath(), e);
        }
        // Создаем директорию заново
        tempPath.toFile().mkdir();

        return tempPath.toFile().getAbsolutePath();
    }
}
