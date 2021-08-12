package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoPropertiesLoader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Тесты для парсера {@link JsonFileDtoReader} и загрузчика данных в связке.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class JsonFileDtoReaderFactoryTest {
    private FileDtoReaderFactory readerFactory;

    @Before
    public void init() {
        readerFactory = new JsonFileDtoReaderFactory(new ObjectMapper());
    }

    /** Проверяет корректность чтения пользователей и групп из импортируемого файла из модели survey. */
    @Test
    public void testSurveyUsersAndGroupsRead() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/json/import1.json");

        final List<ImportDtoInfo> objects = new ArrayList<>();
        ImportDtoInfo checkingUser = null;
        ImportDtoInfo checkingGroup = null;
        final String encoding;
        try (FileDtoReader reader = readerFactory.createReader(file)) {
            encoding = reader.getEncoding();
            ImportDtoInfo dtoInfo;
            while ((dtoInfo = reader.next()) != null) {
                objects.add(dtoInfo);
                if ("c07df913-7c09-43a1-8b89-311fa3d62d55".equals(dtoInfo.getId())
                        && "User".equals(dtoInfo.getType())) {
                    checkingUser = dtoInfo;
                }
                if ("38050a2e-6beb-460b-a019-ead348d0ecc2".equals(dtoInfo.getId())
                        && "Group".equals(dtoInfo.getType())) {
                    checkingGroup = dtoInfo;
                }
            }
        }

        Assert.assertEquals("Считано неверное количество сообщений", 34, objects.size());
        checkUserAndGroup(checkingUser, checkingGroup);

        // удаляем загруженные свойства
        checkingGroup.setDomainTo(null);
        checkingUser.setDomainTo(null);

        // загружаем удаленные свойства
        try (FileDtoPropertiesLoader loader = readerFactory.createLoader(file, encoding)) {
            loader.open();
            loader.loadProperties(checkingGroup);
            loader.loadProperties(checkingUser);
        }

        // проверяем, что свойства загрузились
        checkUserAndGroup(checkingUser, checkingGroup);
    }

    /** Проверяем заженные свойства для пользователя и группы выбранных заранее. */
    private static void checkUserAndGroup(ImportDtoInfo user, ImportDtoInfo group) {
        // проверяем конкретную запись пользователя
        Assert.assertNotNull(user.getDomainTo());
        Assert.assertEquals("Doe28", user.getDomainTo().getProperty("lastName"));
        Assert.assertEquals(4, user.getDomainTo().getProperty("role"));
        Assert.assertNull(user.getDomainTo().getProperty("lastVisit"));
        // проверяем конкретную запись группы
        Assert.assertNotNull(group.getDomainTo());
        Assert.assertEquals("Super Group #0", group.getDomainTo().getProperty("name"));
        Assert.assertEquals(6, group.getDomainTo().getProperty("roles"));
        final List<String> users = (List<String>) group.getDomainTo().getProperty("users");
        Assert.assertEquals(1, users.size());
        Assert.assertTrue(users.contains("592c62f6-66ef-45b4-83e0-c3df40b345da"));
    }

    /** Проверяет, что импортируемый файла с книгами полностью корректно загружается. */
    @Test
    public void testReadBooks() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/json/SimpleJsonImportFile.json");

        final List<ImportDtoInfo> books = new ArrayList<>();
        final String encoding = readBooks(file, books);

        Assert.assertEquals("Считано неверное количество сообщений", 3, books.size());
        checkBook(books.get(0), "Book-ID-1", "Book", "The Divine Comedy");
        checkBook(books.get(1), "Book-ID-2", "Book", "War and Peace");
        checkBook(books.get(2), "Book-ID-3", "Book", "Independent People");

        // удаляем свойства
        books.forEach(book -> book.setDomainTo(null));

        // загружаем удаленные свойства
        try (FileDtoPropertiesLoader loader = readerFactory.createLoader(file, encoding)) {
            loader.open();
            // загружаем в любом порядке
            loader.loadProperties(books.get(2));
            loader.loadProperties(books.get(0));
            loader.loadProperties(books.get(1));
        }

        checkBook(books.get(0), "Book-ID-1", "Book", "The Divine Comedy");
        checkBook(books.get(1), "Book-ID-2", "Book", "War and Peace");
        checkBook(books.get(2), "Book-ID-3", "Book", "Independent People");
    }

    /** Загружает книги из файла в переданную коллекцию.
     * @param file файл.
     * @param books коллекция для загрузки.
     * @return кодировка файла.
     */
    private String readBooks(File file, List<ImportDtoInfo> books) throws ImportParseException, IOException {
        final String encoding;
        try (FileDtoReader reader = readerFactory.createReader(file)) {
            encoding = reader.getEncoding();
            ImportDtoInfo book;
            while ((book = reader.next()) != null) {
                books.add(book);
            }
        }
        return encoding;
    }

    /** Проверяет все поля книги. */
    private void checkBook(ImportDtoInfo book, String id, String type, String title) {
        Assert.assertEquals(id, book.getId());
        Assert.assertEquals(type, book.getType());
        Assert.assertNotNull(book.getDomainTo());
        Assert.assertEquals(id, book.getDomainTo().getId());
        Assert.assertEquals(title, book.getDomainTo().getProperty("title"));
    }


    /** Проверяет, что импортируемый файла с книгами полностью корректно загружается, если грузить группой. */
    @Test
    public void testReadBooksForGroup() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/json/SimpleJsonImportFile.json");

        final List<ImportDtoInfo> books = new ArrayList<>();
        final String encoding = readBooks(file, books);

        Assert.assertEquals("Считано неверное количество сообщений", 3, books.size());
        checkBook(books.get(0), "Book-ID-1", "Book", "The Divine Comedy");
        checkBook(books.get(1), "Book-ID-2", "Book", "War and Peace");
        checkBook(books.get(2), "Book-ID-3", "Book", "Independent People");

        // удаляем свойства
        books.forEach(book -> book.setDomainTo(null));

        // загружаем удаленные свойства
        try (FileDtoPropertiesLoader loader = readerFactory.createLoader(file, encoding)) {
            loader.open();
            loader.loadPropertiesForGroup(books);
        }

        checkBook(books.get(0), "Book-ID-1", "Book", "The Divine Comedy");
        checkBook(books.get(1), "Book-ID-2", "Book", "War and Peace");
        checkBook(books.get(2), "Book-ID-3", "Book", "Independent People");
    }
}
