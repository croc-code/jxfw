package ru.croc.ctp.jxfw.transfer.impl.exp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportDomainToWriter;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService.LocalFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;


/**
 * Абстрактный класс для реализации записи в файл, одного из форматов jxfw.
 *
 * @author Golovin Alexander
 * @since 1.6
 */
public abstract class AbstractFileItemWriter implements AutoCloseable, ExportDomainToWriter {
    private static final Logger log = LoggerFactory.getLogger(AbstractFileItemWriter.class);

    /**
     * Название сценария экспорта.
     */
    private final String scenarioName;
    /**
     * Информация о локальном файле.
     */
    private final LocalFile localFile;
    /**
     * Основной поток дял записи документа.
     */
    private FileChannel channel;
    /**
     * Случайный доступ к файлу.
     */
    private RandomAccessFile randomAccessFile;
    /**
     * Кодировка.
     */
    private String encoding = StandardCharsets.UTF_8.displayName();
    /**
     * Текущая позиция символа для записи.
     */
    private long currentPosition = 0;
    protected final TransferContextService transferContextService;


    /**
     * Создаёт абстрактный класс для реализации записи данных со всех шагов в один файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param scenarioName           имя задачи.
     * @param fileName               имя результирующего файла.
     * @throws IOException ошибка при работе с файловой системой.
     */
    protected AbstractFileItemWriter(TransferContextService transferContextService, String scenarioName, String fileName)
            throws IOException {
        this.transferContextService = transferContextService;
        this.scenarioName = scenarioName;
        localFile = transferContextService.generateLocalFile(String.format("%s.%s", fileName, formatOfFile()));
        createEmptyTemplate();
    }

    /**
     * Создаёт файл с заголовком, без объектов.
     *
     * @throws IOException ошибка при работе с файловой системой.
     */
    private void createEmptyTemplate() throws IOException {
        Files.createFile(Paths.get(localFile.path));
        // записываем заголовок
        final String header = generateHeader();
        open();
        writeToFile(header);
        currentPosition = channel.position();
    }

    /**
     * Записывает данные в файл(между заголовком и закрывающими элемнетами).
     * Note: канал для записи открывается, если он был закрыт. Закрывать writer необходиом явно методом close.
     *
     * @param data объекты сериализованные в строку.
     * @throws IOException ошибка при работе с файловой системой.
     */
    protected void writeData(String data) throws IOException {
        if (channel == null || !channel.isOpen()) {
            open();
        }
        writeToFile(data);
        currentPosition = channel.position();
    }

    /**
     * Возвращает название сценария.
     *
     * @return название сценария.
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Записывает закрывающие теги и закрывает ресурсы.
     *
     * @throws IOException проблем с вводом/выводом.
     */
    public void close() throws IOException {
        if (channel != null) {
            writeToFile(getEndOfFile());

            channel.close();
            channel = null;
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
            randomAccessFile = null;
        }
        log.debug("Channel for file {} is closed.", localFile.path);
    }

    /**
     * Возвращает строку с закрывающими элементами файла соотвествующего формата.
     *
     * @return закрывающие элементы.
     */
    protected abstract String getEndOfFile();

    /**
     * Формирует заголовок файла в виде строки, в формате writer'а.
     *
     * @return заголовок файла.
     */
    protected abstract String generateHeader();

    /**
     * Возвращает Формат writer'а(итогового файла).
     *
     * @return формат, например xml или json.
     */
    protected abstract String formatOfFile();


    /**
     * Открывает каннал для записи данных.
     *
     * @throws IOException проблем с вводом/выводом.
     */
    private void open() throws IOException {
        randomAccessFile = new RandomAccessFile(localFile.path, "rw");
        channel = randomAccessFile.getChannel().position(currentPosition);
        log.debug("Channel for file {} is opened.", localFile.path);
    }

    /**
     * Записывает строку в файл.
     *
     * @param str строка для записи.
     * @throws IOException проблем с вводом/выводом.
     */
    private void writeToFile(String str) throws IOException {
        final byte[] buffer = str.getBytes(encoding);
        channel.write(ByteBuffer.wrap(buffer));
    }

    /**
     * Информация о локальном файле в который проивзодится запись.
     *
     * @return информация о локальном файле.
     */
    public LocalFile getLocalFile() {
        return localFile;
    }

    /**
     * Сохраняет в параметры задачи ид создаваемого ресурса.
     *
     * @param stepExecution выполняемый шаг.
     * @throws UncheckedIOException проблем с вводом/выводом.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            transferContextService.addLocalFiles(stepExecution, Collections.singletonList(getLocalFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Завершает шаг и закрывает ресурсы.
     *
     * @param stepExecution выполняемый шаг.
     * @throws UncheckedIOException проблем с вводом/выводом.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) throws UncheckedIOException {
        try {
            close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return stepExecution.getExitStatus();
    }
}
