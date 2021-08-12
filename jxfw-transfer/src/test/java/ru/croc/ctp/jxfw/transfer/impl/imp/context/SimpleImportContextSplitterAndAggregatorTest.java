package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.LongStream;

/**
 * Проврерка механизма разбиения доменных объектов
 * на не связанные множества {@link SimpleImportContextSplitterAndAggregator}.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public class SimpleImportContextSplitterAndAggregatorTest {
    private SimpleImportContextSplitterAndAggregator splitter;

    @Before
    public void init() {
        splitter = new SimpleImportContextSplitterAndAggregator();
    }

    @Test
    public void testSplitObjectsWithTwoWayConnections() {
        // Создаём объекты
        final ImportDtoInfo book1 = createObject("Book", "id-1");
        final ImportDtoInfo book2 = createObject("Book", "id-2");
        final ImportDtoInfo book3 = createObject("Book", "id-3");
        final ImportDtoInfo author1 = createObject("Author", "id-1");
        final ImportDtoInfo author2 = createObject("Author", "id-2");
        // устанавливаем зависимости между объектами
        book1.getDependencies().add(author1);
        book2.getDependencies().add(author2);
        book3.getDependencies().add(author2);
        author1.getDependencies().add(book1);
        author2.getDependencies().addAll(Arrays.asList(book2, book3));
        // создаем контекси и помещаем в него наши объекты
        final DefaultImportContext context = createContextWithObjects(
                author1, author2,
                book1, book2, book3
        );

        splitter.split(context);

        // проверяем, что объекты разбились на верное количество групп
        final Collection<Set<ImportDtoInfo>> groups = context.getGroupsOfLoading().values();
        Assert.assertEquals(2, groups.size());

        // проверяем, что объекты разбились на корректные группы
        final Set<ImportDtoInfo> group1 = Sets.newHashSet(author1, book1);
        final Set<ImportDtoInfo> group2 = Sets.newHashSet(author2, book2, book3);
        Assert.assertTrue(groups.contains(group1));
        Assert.assertTrue(groups.contains(group2));
    }
    @Test
    public void testAggregate() {
        // Создаём объекты
        final int N = 100;
        final DefaultImportContext context = new DefaultImportContext();
        LongStream.range(0, N)
                .mapToObj(id -> createObject("Book", "Book-ID-" + id))
                .forEach(book -> ImportContextUtils.addOrMergeObject(context, book));

        splitter.split(context);
        Assert.assertEquals(N, context.getGroupsOfLoading().size());

        splitter.setMaxGroupPackageSize(10);
        splitter.aggregate(context);
        Assert.assertEquals(N / 10, context.getGroupsOfLoading().size());

        context.getGroupsOfLoading().values().stream()
                .forEach(group -> Assert.assertEquals(10, group.size()));
    }

    @Test
    public void testSplitObjectsWithOneWayConnections() {
        // Создаём объекты
        final ImportDtoInfo book1 = createObject("Book", "id-1");
        final ImportDtoInfo book2 = createObject("Book", "id-2");
        final ImportDtoInfo book3 = createObject("Book", "id-3");
        final ImportDtoInfo book4 = createObject("Book", "id-4");
        final ImportDtoInfo book5 = createObject("Book", "id-5");
        final ImportDtoInfo author1 = createObject("Author", "id-1");
        final ImportDtoInfo author2 = createObject("Author", "id-2");
        final ImportDtoInfo author3 = createObject("Author", "id-3");
        // устанавливаем зависимости между объектами
        author1.getDependencies().addAll(Arrays.asList(book1, book2));
        author2.getDependencies().addAll(Arrays.asList(book2, book3));
        author3.getDependencies().addAll(Arrays.asList(book4, book5));
        // создаем контекси и помещаем в него наши объекты
        final DefaultImportContext context = createContextWithObjects(
                author1, author2, author3,
                book1, book2, book3, book4, book5
        );

        splitter.split(context);

        // проверяем, что объекты разбились на верное количество групп
        final Collection<Set<ImportDtoInfo>> groups = context.getGroupsOfLoading().values();
        Assert.assertEquals(2, groups.size());

        // проверяем, что объекты разбились на корректные группы
        final Set<ImportDtoInfo> group1 = Sets.newHashSet(author1, author2, book1, book2, book3);
        final Set<ImportDtoInfo> group2 = Sets.newHashSet(author3, book4, book5);
        Assert.assertTrue(groups.contains(group1));
        Assert.assertTrue(groups.contains(group2));
    }

    /** Создаёт контекст импорта с передаными объектами.
     * Внимание: если у нескольких объектов type и id совпадает, то в контексте
     * будет только один объект.
     * @param objects объекты, которые будет в новом контексте.
     * @return новый контекст импорта.
     */
    private DefaultImportContext createContextWithObjects(ImportDtoInfo... objects) {
        final DefaultImportContext context = new DefaultImportContext(null);
        for (ImportDtoInfo object : objects) {
            ImportContextUtils.addOrMergeObject(context, object);
        }
        return context;
    }

    /** Создаёт новый доменный объекта и устанавливает признак загрузки из файла. Если признак
     * установлен не будет, то объект игнорируется при разбиении на группы.
     * @param type тип объекта.
     * @param id  ид объекта.
     * @return новый доменный объекта.
     */
    private ImportDtoInfo createObject(String type, String id) {
        final ImportDtoInfo obj = new ImportDtoInfo(type, id);
        // устанавливаем признак, что объекты загруженны из файла
        obj.setOffsetFirstByteInFile(0);
        obj.setOffsetLastByteInFile(0);
        return obj;
    }
}
