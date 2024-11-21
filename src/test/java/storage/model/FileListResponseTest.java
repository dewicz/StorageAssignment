package storage.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import storage.model.enums.Visibility;

import java.util.Arrays;

public class FileListResponseTest {

    private FileListResponse fileListResponse;

    @Before
    public void setUp() {
        fileListResponse = new FileListResponse("testFile.txt", Visibility.PUBLIC,
                Arrays.asList("tag1", "tag2"), "user123", 1024L, "http://localhost/testFile.txt");
    }

    @Test
    public void testConstructor() {
        assertNotNull(fileListResponse);
        assertEquals("testFile.txt", fileListResponse.getFilename());
        assertEquals(Visibility.PUBLIC, fileListResponse.getVisibility());
        assertEquals(Arrays.asList("tag1", "tag2"), fileListResponse.getTags());
        assertEquals("user123", fileListResponse.getUser());
        assertEquals(1024L, fileListResponse.getSize());
        assertEquals("http://localhost/testFile.txt", fileListResponse.getDownloadLink());
    }

    @Test
    public void testGetFilename() {
        assertEquals("testFile.txt", fileListResponse.getFilename());
    }

    @Test
    public void testSetFilename() {
        fileListResponse.setFilename("newFile.txt");
        assertEquals("newFile.txt", fileListResponse.getFilename());
    }

    @Test
    public void testGetVisibility() {
        assertEquals(Visibility.PUBLIC, fileListResponse.getVisibility());
    }

    @Test
    public void testSetVisibility() {
        fileListResponse.setVisibility(Visibility.PRIVATE);
        assertEquals(Visibility.PRIVATE, fileListResponse.getVisibility());
    }

    @Test
    public void testGetTags() {
        assertEquals(Arrays.asList("tag1", "tag2"), fileListResponse.getTags());
    }

    @Test
    public void testSetTags() {
        fileListResponse.setTags(Arrays.asList("tag3", "tag4"));
        assertEquals(Arrays.asList("tag3", "tag4"), fileListResponse.getTags());
    }

    @Test
    public void testGetUser() {
        assertEquals("user123", fileListResponse.getUser());
    }

    @Test
    public void testSetUser() {
        fileListResponse.setUser("user456");
        assertEquals("user456", fileListResponse.getUser());
    }

    @Test
    public void testGetDownloadLink() {
        assertEquals("http://localhost/testFile.txt", fileListResponse.getDownloadLink());
    }

    @Test
    public void testSetDownloadLink() {
        fileListResponse.setDownloadLink("http://localhost/newFile.txt");
        assertEquals("http://localhost/newFile.txt", fileListResponse.getDownloadLink());
    }

    @Test
    public void testGetSize() {
        assertEquals(1024L, fileListResponse.getSize());
    }

    @Test
    public void testSetSize() {
        fileListResponse.setSize(2048L);
        assertEquals(2048L, fileListResponse.getSize());
    }

    @Test
    public void testDefaultConstructor() {
        FileListResponse defaultResponse = new FileListResponse();
        assertNull(defaultResponse.getFilename());
        assertNull(defaultResponse.getVisibility());
        assertNull(defaultResponse.getTags());
        assertNull(defaultResponse.getUser());
        assertEquals(0L, defaultResponse.getSize());
        assertNull(defaultResponse.getDownloadLink());
    }
}
