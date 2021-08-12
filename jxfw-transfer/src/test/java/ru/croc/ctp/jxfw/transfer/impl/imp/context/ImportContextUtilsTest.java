package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Тестирование вспомогательных функции для работы с {@link DefaultImportContext}.
 * @author Alexander Golovin
 * @since 1.5
 */
public class ImportContextUtilsTest {
    private final String type = "User";
    private final String id = "user-ID-1";
    private DefaultImportContext context;

    private ImportDtoInfo group1;
    private ImportDtoInfo group2;
    private ImportDtoInfo group3;

    @Before
    public void init() {
        context = new DefaultImportContext(null);

        group1 = new ImportDtoInfo("Group", "group-ID-1");
        group2 = new ImportDtoInfo("Group", "group-ID-2");
        group3 = new ImportDtoInfo("Group", "group-ID-3");
    }

    @Test
    public void getOrCreateObject() {
        // создаётся новый user
        ImportDtoInfo userActual = ImportContextUtils.getOrCreateObject(context, type, id);
        ImportDtoInfo userExpected = context.getObjects().get(type).get(id);
        Assert.assertEquals(userExpected, userActual);
        checkEmptyUser(userActual);
        // получаем существующий user
        userActual = ImportContextUtils.getOrCreateObject(context, type, id);
        Assert.assertEquals(userExpected, userActual);
        checkEmptyUser(userActual);
    }

    private void checkEmptyUser(ImportDtoInfo user) {
        Assert.assertEquals(type, user.getType());
        Assert.assertEquals(id, user.getId());

        Assert.assertNull(user.getDomainTo());
        Assert.assertFalse(user.isLoadFromFile());

        Assert.assertTrue(user.getDependencies().isEmpty());
        Assert.assertTrue(user.getDependenciesOfNotResolvedInSource().isEmpty());
        Assert.assertTrue(user.getDependenciesOfNotResolved().isEmpty());
    }


    @Test
    public void findObject() {
        // ищем не существующий user
        Assert.assertNull(ImportContextUtils.findObject(context, type, id));

        // создаём user
        ImportContextUtils.getOrCreateObject(context, type, id);

        // ищем существующий user
        ImportDtoInfo userActual = ImportContextUtils.getOrCreateObject(context, type, id);
        ImportDtoInfo userExpected = context.getObjects().get(type).get(id);
        Assert.assertEquals(userExpected, userActual);
    }

    @Test
    public void addOrMergeObject() {
        final ImportDtoInfo user1 = new ImportDtoInfo(type, id);
        // инициализация объекта
        final DomainTo dto1 = new DomainTo();
        user1.setOffsetFirstByteInFile(2);
        user1.setOffsetLastByteInFile(1);
        user1.setDomainTo(dto1);
        user1.getDependencies().addAll(Sets.newSet(group1, group2));
        user1.getDependenciesOfNotResolvedInSource().addAll(Sets.newSet(group2, group3));
        user1.getDependenciesOfNotResolved().addAll(Sets.newSet(group3, group1));


        // проверяем добавление
        ImportDtoInfo actualUser = ImportContextUtils.addOrMergeObject(context, user1);
        Assert.assertEquals(user1, actualUser);
        // проверяем не изменчивость полей
        Assert.assertEquals(type, actualUser.getType());
        Assert.assertEquals(id, actualUser.getId());

        Assert.assertEquals(dto1, actualUser.getDomainTo());
        Assert.assertTrue(actualUser.isLoadFromFile());

        Assert.assertEquals(2, actualUser.getOffsetFirstByteInFile());
        Assert.assertEquals(1, actualUser.getOffsetLastByteInFile());

        Assert.assertEquals(Sets.newSet(group1, group2), actualUser.getDependencies());
        Assert.assertEquals(Sets.newSet(group2, group3), actualUser.getDependenciesOfNotResolvedInSource());
        Assert.assertEquals(Sets.newSet(group3, group1), actualUser.getDependenciesOfNotResolved());

        final ImportDtoInfo user2 = new ImportDtoInfo(type, id);
        // инициализация объекта
        final DomainTo dto2 = new DomainTo();
        user2.setOffsetFirstByteInFile(3);
        user2.setOffsetLastByteInFile(4);
        user2.setDomainTo(dto2);
        user2.getDependencies().addAll(Sets.newSet(group3));
        user2.getDependenciesOfNotResolvedInSource().addAll(Sets.newSet(group3, group1));
        user2.getDependenciesOfNotResolved().addAll(Sets.newSet(group1));

        // проверяем добавление
        actualUser = ImportContextUtils.addOrMergeObject(context, user2);
        Assert.assertNotEquals(user2, actualUser);
        Assert.assertEquals(user1, actualUser);
        // проверяем не изменчивость полей
        Assert.assertEquals(type, actualUser.getType());
        Assert.assertEquals(id, actualUser.getId());

        Assert.assertEquals(dto2, actualUser.getDomainTo());
        Assert.assertTrue(actualUser.isLoadFromFile());

        Assert.assertEquals(3, actualUser.getOffsetFirstByteInFile());
        Assert.assertEquals(4, actualUser.getOffsetLastByteInFile());

        Assert.assertEquals(user1.getDependencies(), actualUser.getDependencies());
        Assert.assertEquals(user1.getDependenciesOfNotResolvedInSource(), actualUser.getDependenciesOfNotResolvedInSource());
        Assert.assertEquals(user1.getDependenciesOfNotResolved(), actualUser.getDependenciesOfNotResolved());
    }

    @Test
    public void removeDependencyFromDependenciesOfSource() {
        // инициализируем контекст
        final ImportDtoInfo user1 = ImportContextUtils.addOrMergeObject(context, new ImportDtoInfo("User", "user-id-1"));
        final ImportDtoInfo user2 = ImportContextUtils.addOrMergeObject(context, new ImportDtoInfo("User", "user-id-2"));
        final ImportDtoInfo user3 = ImportContextUtils.addOrMergeObject(context, new ImportDtoInfo("User", "user-id-3"));
        group1 = ImportContextUtils.addOrMergeObject(context, group1);
        // заполняем списки зависимости для user1
        user1.getDependencies().addAll(Sets.newSet(user2, user3, group1));
        user1.getDependenciesOfNotResolvedInSource().addAll(Sets.newSet(group1, user2));
        user1.getDependenciesOfNotResolved().addAll(Sets.newSet(group1));
        // заполняем списки зависимости для user2
        user2.getDependencies().addAll(Sets.newSet(user1, user3, group2));
        user2.getDependenciesOfNotResolvedInSource().addAll(Sets.newSet(group2, user1, group3));
        user2.getDependenciesOfNotResolved().addAll(Sets.newSet(group2, user3));
        // заполняем списки зависимости для group1
        group1.getDependencies().addAll(Sets.newSet(user1, user3, group2));
        group1.getDependenciesOfNotResolvedInSource().addAll(Sets.newSet(user2));
        group1.getDependenciesOfNotResolved().addAll(Sets.newSet(group2));

        // удаляем объект
        ImportContextUtils.removeDependencyFromDependenciesOfSource(context, group2);

        // проверяем списки user1, после удаления group2
        Assert.assertEquals(Sets.newSet(user2, user3, group1), user1.getDependencies());//неизменна
        Assert.assertEquals(Sets.newSet(group1, user2), user1.getDependenciesOfNotResolvedInSource());
        Assert.assertEquals(Sets.newSet(group1), user1.getDependenciesOfNotResolved());
        // проверяем списки user2, после удаления group2
        Assert.assertEquals(Sets.newSet(user1, user3, group2), user2.getDependencies());//неизменна
        Assert.assertEquals(Sets.newSet(user1, group3), user2.getDependenciesOfNotResolvedInSource());
        Assert.assertEquals(Sets.newSet(user3), user2.getDependenciesOfNotResolved());
        // проверяем списки group1, после удаления group2
        Assert.assertEquals(Sets.newSet(user1, user3, group2), group1.getDependencies());//неизменна
        Assert.assertEquals(Sets.newSet(user2), group1.getDependenciesOfNotResolvedInSource());
        Assert.assertEquals(Sets.newSet(), group1.getDependenciesOfNotResolved());
    }

    @Test
    public void clear() {
        ImportContextUtils.addOrMergeObject(context, new ImportDtoInfo("User", "user-id-1"));
        ImportContextUtils.addOrMergeObject(context, new ImportDtoInfo("User", "user-id-2"));
        ImportContextUtils.addOrMergeObject(context, group1);
        context.getGroupsOfLoading().put(0L, new HashSet<>());
        context.getGroupsOfLoading().put(1L, new HashSet<>());
        context.getGroupsOfLoading().put(2L, new HashSet<>());

        //проверяем наличение объектов
        Assert.assertFalse(context.getObjects().isEmpty());
        Assert.assertFalse(context.getGroupsOfLoading().isEmpty());
        // чистим
        ImportContextUtils.clear(context);
        //проверяем отсутсвие объектов
        Assert.assertTrue(context.getObjects().isEmpty());
        Assert.assertTrue(context.getGroupsOfLoading().isEmpty());
    }
}