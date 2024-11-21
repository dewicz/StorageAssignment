package storage.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import storage.model.enums.Visibility;

import java.util.Arrays;

public class FileMetadataTest {

    private FileMetadata fileMetadata;

    @Before
    public void setUp() {
        fileMetadata = new FileMetadata();
        fileMetadata.setId("123");
        fileMetadata.setFileId("fileId123");
        fileMetadata.setFilename("testFile.txt");
        fileMetadata.setVisibility(Visibility.PUBLIC);
        fileMetadata.setTags(Arrays.asList("tag1", "tag2"));
        fileMetadata.setUser("user123");
        fileMetadata.setSize(1024L);
    }

    @Test
    public void testGetId() {
        assertEquals("123", fileMetadata.getId());
    }

    @Test
    public void testSetId() {
        fileMetadata.setId("456");
        assertEquals("456", fileMetadata.getId());
    }

    @Test
    public void testGetFileId() {
        assertEquals("fileId123", fileMetadata.getFileId());
    }

    @Test
    public void testSetFileId() {
        fileMetadata.setFileId("newFileId");
        assertEquals("newFileId", fileMetadata.getFileId());
    }

    @Test
    public void testGetFilename() {
        assertEquals("testFile.txt", fileMetadata.getFilename());
    }

    @Test
    public void testSetFilename() {
        fileMetadata.setFilename("newTestFile.txt");
        assertEquals("newTestFile.txt", fileMetadata.getFilename());
    }

    @Test
    public void testGetVisibility() {
        assertEquals(Visibility.PUBLIC, fileMetadata.getVisibility());
    }

    @Test
    public void testSetVisibility() {
        fileMetadata.setVisibility(Visibility.PRIVATE);
        assertEquals(Visibility.PRIVATE, fileMetadata.getVisibility());
    }

    @Test
    public void testGetTags() {
        assertEquals(Arrays.asList("tag1", "tag2"), fileMetadata.getTags());
    }

    @Test
    public void testSetTags() {
        fileMetadata.setTags(Arrays.asList("tag3", "tag4"));
        assertEquals(Arrays.asList("tag3", "tag4"), fileMetadata.getTags());
    }

    @Test
    public void testGetUser() {
        assertEquals("user123", fileMetadata.getUser());
    }

    @Test
    public void testSetUser() {
        fileMetadata.setUser("user456");
        assertEquals("user456", fileMetadata.getUser());
    }

    @Test
    public void testGetSize() {
        assertEquals(1024L, fileMetadata.getSize());
    }

    @Test
    public void testSetSize() {
        fileMetadata.setSize(2048L);
        assertEquals(2048L, fileMetadata.getSize());
    }

    @Test
    public void testDefaultConstructor() {
        FileMetadata defaultMetadata = new FileMetadata();
        assertNull(defaultMetadata.getId());
        assertNull(defaultMetadata.getFileId());
        assertNull(defaultMetadata.getFilename());
        assertNull(defaultMetadata.getVisibility());
        assertNull(defaultMetadata.getTags());
        assertNull(defaultMetadata.getUser());
        assertEquals(0L, defaultMetadata.getSize());
    }
}
