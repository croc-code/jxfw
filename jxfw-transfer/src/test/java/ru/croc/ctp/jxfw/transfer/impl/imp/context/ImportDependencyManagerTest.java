package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.handler.DefaultImportDependencyCollisionHandler;

import java.util.Arrays;

/**
 * Тесты для менеджера зависимостей объектов.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class ImportDependencyManagerTest {
    private DefaultImportDependencyManager importDependencyManager;

    private DefaultImportContext context;
    // тестовые ImportDtoInfo
    private ImportDtoInfo user1;
    private ImportDtoInfo user2;
    private ImportDtoInfo user3;
    private ImportDtoInfo user4;
    private ImportDtoInfo group1;
    private ImportDtoInfo group2;
    private ImportDtoInfo group3;

    @Before
    public void init() {
        importDependencyManager = new DefaultImportDependencyManager();
        context = new DefaultImportContext(null);
        // add users
        user1 = ImportContextUtils.addOrMergeObject(context, createObject("User", "user-ID-1"));
        user2 = ImportContextUtils.addOrMergeObject(context, createObject("User", "user-ID-2"));
        user3 = ImportContextUtils.addOrMergeObject(context, createObject("User", "user-ID-3"));
        user4 = ImportContextUtils.addOrMergeObject(context, createObject("User", "user-ID-4"));
        // add groups
        group1 = ImportContextUtils.addOrMergeObject(context, createObject("Group", "group-ID-1"));
        group2 = ImportContextUtils.addOrMergeObject(context, createObject("Group", "group-ID-2"));
        group3 = ImportContextUtils.addOrMergeObject(context, createObject("Group", "group-ID-3"));
    }

    private ImportDtoInfo createObject(String type, String id) {
        final ImportDtoInfo obj = new ImportDtoInfo(type, id);
        // устанавливаем признак, что объекты загруженны из файла
        obj.setOffsetFirstByteInFile(0);
        obj.setOffsetLastByteInFile(0);
        return obj;
    }

    @Test(expected = RuntimeException.class)
    public void testFewNotResolveDependencies() {
        user1.getDependenciesOfNotResolved().addAll(Arrays.asList(user2, user3, group2));
        group1.getDependenciesOfNotResolved().addAll(Arrays.asList(user2, group2));
        new SimpleImportContextSplitterAndAggregator().split(context);
        importDependencyManager.setHandler(new DefaultImportDependencyCollisionHandler());
        importDependencyManager.resolve(context);
    }

    @Test
    public void testAllDependenciesAreResolved() {
        importDependencyManager.setHandler(essues -> {
            Assert.assertTrue(essues.isEmpty());
            return true;
        });
        importDependencyManager.resolve(context);
    }

    @Test(expected = RuntimeException.class)
    public void testFewNotResolveDependenciesWithNotLoadedDto() {
        user1.getDependenciesOfNotResolved().addAll(Arrays.asList(user2, user3, group2));
        group1.getDependenciesOfNotResolved().addAll(Arrays.asList(user2, user3, user4));
        group1.setOffsetFirstByteInFile(ImportDtoInfo.NOT_LOADED);
        group1.setOffsetLastByteInFile(ImportDtoInfo.NOT_LOADED);
        new SimpleImportContextSplitterAndAggregator().split(context);

        importDependencyManager.setHandler(new DefaultImportDependencyCollisionHandler());
        importDependencyManager.resolve(context);
    }
}
