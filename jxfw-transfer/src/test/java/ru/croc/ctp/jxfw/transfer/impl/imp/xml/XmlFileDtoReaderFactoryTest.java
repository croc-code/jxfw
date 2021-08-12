package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoPropertiesLoader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.json.JsonFileDtoReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Тесты для парсера {@link JsonFileDtoReader} и загрузчика данных в связке.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class XmlFileDtoReaderFactoryTest {
    private FileDtoReaderFactory readerFactory;

    @Before
    public void init() {
        readerFactory = new XmlFileDtoReaderFactory();
    }

    /** Проверяет корректность чтения одного объекта из импортируемого файла. */
    @Test
    public void testOneElement() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/xml/one-element.xml");

        final List<ImportDtoInfo> objects = new ArrayList<>();
        final String encoding = readObjects(file, objects);

        Assert.assertEquals("Считано неверное количество сообщений", 1, objects.size());
        final ImportDtoInfo checkObject = objects.get(0);
        checkOneElement(checkObject);
        checkObject.setDomainTo(null);

        // загружаем удаленные свойства
        try (FileDtoPropertiesLoader loader = readerFactory.createLoader(file, encoding)) {
            loader.open();
            loader.loadProperties(checkObject);
        }
        checkOneElement(checkObject);
    }

    /** Функиция проверки объекта для файла xml/one-element.xml. */
    private void checkOneElement(ImportDtoInfo checkObject) {
        Assert.assertEquals("8de05a6e-b0dd-49fe-a0dc-0aa53bdb0504", checkObject.getId());
        Assert.assertEquals("Amnesty", checkObject.getType());
        Assert.assertNotNull(checkObject.getDomainTo());
        Assert.assertEquals("Код1", checkObject.getDomainTo().getProperty("Code"));
        Assert.assertEquals("Name1", checkObject.getDomainTo().getProperty("Name"));
        Assert.assertEquals("2006-09-22T00:00:00.000", checkObject.getDomainTo().getProperty("Date"));
        Assert.assertEquals("Описание 1", checkObject.getDomainTo().getProperty("Description"));
    }


    /** Проверяет, что импортируемый файла с книгами полностью корректно загружается. */
    @Test
    public void testReadSimpleBooks() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/xml/books1.xml");
        final List<ImportDtoInfo> books = new ArrayList<>();
        final String encoding = readObjects(file, books);

        Assert.assertEquals("Считано неверное количество сообщений", 3, books.size());
        final ImportDtoInfo book1 = findObjectByTypeAndId(books, "Book", "Book-ID-1");
        final ImportDtoInfo book2 = findObjectByTypeAndId(books, "Book", "Book-ID-2");
        final ImportDtoInfo book3 = findObjectByTypeAndId(books, "Book", "Book-ID-3");

        checkBook(book1, "The Divine Comedy");
        checkBook(book2, "War and Peace");
        checkBook(book3, "Independent People");

        // удаляем свойства
        books.forEach(book -> book.setDomainTo(null));
        // загружаем удаленные свойства
        loadProperties(file, books, encoding);

        checkBook(book1, "The Divine Comedy");
        checkBook(book2, "War and Peace");
        checkBook(book3, "Independent People");
    }

    /** Проверяет, что импортируемый файла с книгами и авторами полностью корректно загружается.
     * Проверяется загрузка связей между объектами.
     */
    @Test
    public void testReadBooksAndAuthors() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/xml/books2.xml");
        final List<ImportDtoInfo> objects = new ArrayList<>();
        final String encoding = readObjects(file, objects);

        Assert.assertEquals("Считано неверное количество сообщений", 7, objects.size());
        final ImportDtoInfo book1 = findObjectByTypeAndId(objects, "Book", "Book-ID-1");
        final ImportDtoInfo book2 = findObjectByTypeAndId(objects, "Book", "Book-ID-2");
        final ImportDtoInfo book3 = findObjectByTypeAndId(objects, "Book", "Book-ID-3");
        final ImportDtoInfo book4 = findObjectByTypeAndId(objects, "Book", "Book-ID-4");
        final ImportDtoInfo author1 = findObjectByTypeAndId(objects, "Author", "Author-ID-1");
        final ImportDtoInfo author2 = findObjectByTypeAndId(objects, "Author", "Author-ID-2");
        final ImportDtoInfo author3 = findObjectByTypeAndId(objects, "Author", "Author-ID-3");

        checkBook(book1, "The Divine Comedy", "Author-ID-1");
        checkBook(book2, "War and Peace", "Author-ID-2");
        checkBook(book3, "Independent People", "Author-ID-3");
        checkBook(book4, "Анна Каренина", "Author-ID-2");
        checkAuthor(author1, "Данте Алигьери", Sets.newHashSet("Book-ID-1"));
        checkAuthor(author2, "Лев Николаевич Толстой", Sets.newHashSet("Book-ID-2", "Book-ID-4"));
        checkAuthor(author3, "Лакснесс, Халлдор Кильян", Sets.newHashSet("Book-ID-3"));

        // удаляем свойства
        objects.forEach(obj -> obj.setDomainTo(null));
        // загружаем удаленные свойства
        loadProperties(file, objects, encoding);

        checkBook(book1, "The Divine Comedy", "Author-ID-1");
        checkBook(book2, "War and Peace", "Author-ID-2");
        checkBook(book3, "Independent People", "Author-ID-3");
        checkBook(book4, "Анна Каренина", "Author-ID-2");
        checkAuthor(author1, "Данте Алигьери", Sets.newHashSet("Book-ID-1"));
        checkAuthor(author2, "Лев Николаевич Толстой", Sets.newHashSet("Book-ID-2", "Book-ID-4"));
        checkAuthor(author3, "Лакснесс, Халлдор Кильян", Sets.newHashSet("Book-ID-3"));
    }


    /** Читает последовательно все объекты из файла.
     * @param file загружаемый ресурс.
     * @param objects коллекция в которую грузятся объекты.
     * @return кодировка файла.
     */
    private String readObjects(File file, List<ImportDtoInfo> objects) throws ImportParseException, IOException {
        String encoding;
        try (FileDtoReader reader = readerFactory.createReader(file)) {
            encoding = reader.getEncoding();
            ImportDtoInfo object;
            while ((object = reader.next()) != null) {
                objects.add(object);
            }
        }
        return encoding;
    }

    /** Загружает свойства объектов в случайном порядке.
     * @param file загружаемый ресурс.
     * @param objects список для объекто для которых грузятся свойства.
     * @param encoding кодировка файла.
     */
    private void loadProperties(File file, List<ImportDtoInfo> objects, String encoding) throws IOException, ImportParseException {
        try (FileDtoPropertiesLoader loader = readerFactory.createLoader(file, encoding)) {
            loader.open();
            // загружаем в любом порядке
            Collections.shuffle(objects);
            for (ImportDtoInfo obj : objects) {
                loader.loadProperties(obj);
            }
        }
    }

    /** Находит {@link ImportDtoInfo} в списке по типу и ид.
     * @param objects список объектов.
     * @param type тип.
     * @param id  ид.
     * @return объект или null.
     */
    private static ImportDtoInfo findObjectByTypeAndId(List<ImportDtoInfo> objects, String type, String id) {
        return objects.stream()
                .filter(o -> type.equals(o.getType()) && id.equals(o.getId()))
                .findFirst()
                .orElse(null);
    }

    /** Проверяет все поля автора.
     * @param author проверяемый объект автора.
     * @param name ожидаемое значение имени.
     * @param books ожидаемый списко книг.
     */
    private void checkAuthor(ImportDtoInfo author, String name, Set<String> books) {
        Assert.assertNotNull(author);
        Assert.assertNotNull(author.getDomainTo());
        Assert.assertEquals(author.getId(), author.getDomainTo().getId());
        Assert.assertEquals(author.getType(), author.getDomainTo().getType());
        Assert.assertEquals(name, author.getDomainTo().getProperty("name"));
        final Set<String> actualBooks;
        if (author.getDomainTo().getProperty("books") instanceof String) {
            // если один элемент он записывается как String, в лист превращается в AuthorService.fromTo()
            actualBooks = Sets.newHashSet((String) author.getDomainTo().getProperty("books"));
        } else {
            // если больше одного элемента, то список
            actualBooks = new HashSet<>((List) author.getDomainTo().getProperty("books"));
        }
        Assert.assertEquals(books, actualBooks);
    }

    /** Проверяет все поля книги.
     * @param book проверяемый объект книга.
     * @param title ожидаемое значение заглавия.
     */
    private void checkBook(ImportDtoInfo book, String title) {
        Assert.assertNotNull(book);
        Assert.assertNotNull(book.getDomainTo());
        Assert.assertEquals(book.getId(), book.getDomainTo().getId());
        Assert.assertEquals(book.getType(), book.getDomainTo().getType());
        Assert.assertEquals(title, book.getDomainTo().getProperty("title"));
    }

    /** Проверяет все поля книги.
     * @param book проверяемый объект книга.
     * @param title ожидаемое значение заглавия.
     * @param author ожидаемыое значение автор.
     */
    private void checkBook(ImportDtoInfo book, String title, String author) {
        this.checkBook(book, title);
        Assert.assertEquals(author, book.getDomainTo().getProperty("author"));
    }

    /** JXFW-823: Проверяет корректность чтения пустых числовых тегов и
     * тегов без dt и oid. */
    @Test
    public void testJXFW823EmptyTypes() throws IOException, ImportParseException {
        final File file = new File("src/test/resources/xml/jxfw-823.xml");

        final List<ImportDtoInfo> objects = new ArrayList<>();
        final String encoding = readObjects(file, objects);

        Assert.assertEquals("Считано неверное количество сообщений", 2, objects.size());
        Assert.assertNull(objects.get(0).getDomainTo().getProperty("title"));
        checkEmptyTypes(objects.get(0).getDomainTo());
        checkEmptyTypes(objects.get(1).getDomainTo());


        // загружаем удаленные свойства
        try (FileDtoPropertiesLoader loader = readerFactory.createLoader(file, encoding)) {
            loader.open();
            loader.loadPropertiesForGroup(objects);
        }
        // проверка после догрузки свойств
        Assert.assertNull(objects.get(0).getDomainTo().getProperty("title"));
        checkEmptyTypes(objects.get(0).getDomainTo());
        checkEmptyTypes(objects.get(1).getDomainTo());
    }

    /** Проверяет заполнение {@link DomainTo} пустыми значениями.
     * @param dto DTO объект.
     */
    private void checkEmptyTypes(DomainTo dto) {
        Assert.assertNull(dto.getProperty("a1"));
        Assert.assertEquals(0L, dto.getProperty("a2"));
        Assert.assertEquals(0, dto.getProperty("a3"));
        Assert.assertEquals(0, dto.getProperty("a4"));
        Assert.assertNull(dto.getProperty("a5"));
        Assert.assertEquals(0.0d, dto.getProperty("a6"));
        Assert.assertEquals(0, dto.getProperty("a7"));
    }
}
