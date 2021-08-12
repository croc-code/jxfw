package ru.croc.ctp.jxfw.transfer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.StreamUtils;
import ru.croc.ctp.jxfw.core.facade.webclient.file.LocalResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static ru.croc.ctp.jxfw.transfer.impl.TransferContextService.*;

public class TransferContextServiceTest {
    private TransferContextService service;
    private ResourceStore resourceStore;
    private Path pathOfResourceStore;
    private Path pathOfLocalFiles;

    @Before
    public void init() throws Exception {
        pathOfResourceStore = Files.createTempDirectory("test");
        pathOfLocalFiles = Files.createTempDirectory("test2");
        resourceStore = new LocalResourceStore(pathOfResourceStore.toString(), 1024*1024*100L);
        service = new TransferContextService(resourceStore, new ObjectMapper(),
                pathOfLocalFiles.toString(), 10L, 100L);
    }

    @After
    public void destroy() throws Exception {
        if (pathOfLocalFiles.toFile().exists()) {
            pathOfLocalFiles.toFile().delete();
        }
        if (pathOfResourceStore.toFile().exists()) {
            pathOfResourceStore.toFile().delete();
        }
    }

    /**
     * Проверка очистки файлов в директории, если они не изменялись дольше заданного времени.
     */
    @Test
    public void cleanTest() throws Exception {
        final Path directory = Files.createTempDirectory("test");
        final Long storeTime = 100L;
        final TransferContextService service = new TransferContextService(null, null,
                directory.toString(), 10000L, storeTime);

        Files.createFile(Paths.get(directory.toString(), "file1.txt"));
        Files.createFile(Paths.get(directory.toString(), "file2.txt"));
        Files.createFile(Paths.get(directory.toString(), "file3.txt"));
        Thread.sleep(storeTime);

        Assert.assertEquals(3, directory.toFile().listFiles().length);
        service.cleanDirectory(storeTime);
        Assert.assertEquals(0, directory.toFile().listFiles().length);
        Files.deleteIfExists(directory);
    }

    /**
     * Проверка правильной генерации {@link LocalFile}.
     */
    @Test
    public void generateLocalFileTest() throws Exception {
        final String fileName = "file.txt";
        final LocalFile localFile = service.generateLocalFile(fileName);

        Assert.assertEquals(fileName, localFile.fileName);
        Assert.assertEquals(pathOfLocalFiles.toAbsolutePath().toString(),
                Paths.get(localFile.path).getParent().toAbsolutePath().toString());
        Assert.assertTrue(localFile.path.endsWith(fileName));
    }

    /** Проверка разных вариаций использования параметра с локальными файлами. */
    @Test
    public void useLocalFilesTest() throws Exception {
        final ExecutionContext executionContext = new ExecutionContext();
        final LocalFile localFile1 = service.generateLocalFile("file.json");
        final LocalFile localFile2 = service.generateLocalFile("file.json");

        Assert.assertTrue(service.getLocalFiles(executionContext).isEmpty());

        service.setLocalFiles(executionContext, Arrays.asList(localFile1, localFile2, localFile1));
        Assert.assertEquals(Arrays.asList(localFile1, localFile2), service.getLocalFiles(executionContext));

        final LocalFile localFile3 = service.generateLocalFile("file2.json");
        service.addLocalFiles(executionContext, Arrays.asList(localFile3, localFile2, localFile3));
        Assert.assertEquals(Arrays.asList(localFile1, localFile2, localFile3), service.getLocalFiles(executionContext));
        Assert.assertEquals(localFile1, service.getFirstLocalFile(executionContext).orElse(null));
    }

    /** Проверка инициализации директории для хранения файлов. */
    @Test
    public void createDirectoryTest() throws Exception {
        Assert.assertTrue(pathOfLocalFiles.toFile().exists());
    }

    /** Проверка копирования файла в хранилище ресурсов. */
    @Test
    public void loadLocalFileToResourceStoreTest() throws Exception {
        final ExecutionContext executionContext = new ExecutionContext();
        final LocalFile localFile = service.generateLocalFile("file.json");
        final String text = "Test text!";
        Files.write(Paths.get(localFile.path), text.getBytes(StandardCharsets.UTF_8));
        service.setLocalFiles(executionContext, Arrays.asList(localFile));

        final String resourceId = service.loadLocalFileToResourceStore(executionContext);
        final ResourceProperties resourceProperties = resourceStore.getResourceProperties(resourceId);
        Assert.assertEquals("file.json", resourceProperties.getFileName());
        Assert.assertEquals("json", resourceProperties.getContentType());
        Assert.assertEquals(text.getBytes(StandardCharsets.UTF_8).length, (long)resourceProperties.getContentSize());
        Assert.assertEquals(
                text,
                StreamUtils.copyToString(resourceStore.getResourceStream(resourceId), StandardCharsets.UTF_8)
        );
    }
}
