package ru.croc.ctp.jxfw.core.facade.webclient.file;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Модульные тесты для {@link ResourceStoreBlob}
 * <p/>
 * Created by SPlaunov on 14.06.2016.
 */
public class ResourceStoreBlobTest {
	
    @Test
    public void getFileLength() throws SQLException, IOException {
        //given:
    	ResourceProperties resourceProperties = new ResourceProperties(null, "file.txt", 333L);        
        String resourceId = "res1";
    	
    	ResourceStore fileStore = Mockito.mock(ResourceStore.class);
        Mockito.when(fileStore.getResourceProperties(resourceId)).thenReturn(resourceProperties);
        
        Blob blob = new ResourceStoreBlob(fileStore, resourceId);

        //when:
        long lenght = blob.length();

        //then:
        assertEquals(333L, lenght);
    }
}
