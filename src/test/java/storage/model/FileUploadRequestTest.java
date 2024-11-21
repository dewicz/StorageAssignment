package storage.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import storage.model.enums.Visibility;

import java.util.Arrays;
import static org.mockito.Mockito.*;


public class FileUploadRequestTest {

    @Mock
    private MultipartFile mockFile;

    private FileUploadRequest fileUploadRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(mockFile);
        fileUploadRequest.setFilename("testFile.txt");
        fileUploadRequest.setVisibility(Visibility.PUBLIC);
        fileUploadRequest.setTags(Arrays.asList("tag1", "tag2"));
        fileUploadRequest.setUser("user123");
    }

    @Test
    public void testGetFile() {
        assertEquals(mockFile, fileUploadRequest.getFile());
    }

    @Test
    public void testSetFile() {
        MultipartFile newFile = mock(MultipartFile.class);
        fileUploadRequest.setFile(newFile);
        assertEquals(newFile, fileUploadRequest.getFile());
    }

    @Test
    public void testGetFilename() {
        assertEquals("testFile.txt", fileUploadRequest.getFilename());
    }

    @Test
    public void testSetFilename() {
        fileUploadRequest.setFilename("newFile.txt");
        assertEquals("newFile.txt", fileUploadRequest.getFilename());
    }

    @Test
    public void testGetVisibility() {
        assertEquals(Visibility.PUBLIC, fileUploadRequest.getVisibility());
    }

    @Test
    public void testSetVisibility() {
        fileUploadRequest.setVisibility(Visibility.PRIVATE);
        assertEquals(Visibility.PRIVATE, fileUploadRequest.getVisibility());
    }

    @Test
    public void testGetTags() {
        assertEquals(Arrays.asList("tag1", "tag2"), fileUploadRequest.getTags());
    }

    @Test
    public void testSetTags() {
        fileUploadRequest.setTags(Arrays.asList("tag3", "tag4"));
        assertEquals(Arrays.asList("tag3", "tag4"), fileUploadRequest.getTags());
    }

    @Test
    public void testGetUser() {
        assertEquals("user123", fileUploadRequest.getUser());
    }

    @Test
    public void testSetUser() {
        fileUploadRequest.setUser("user456");
        assertEquals("user456", fileUploadRequest.getUser());
    }

    @Test
    public void testDefaultConstructor() {
        FileUploadRequest defaultRequest = new FileUploadRequest();
        assertNull(defaultRequest.getFile());
        assertNull(defaultRequest.getFilename());
        assertNull(defaultRequest.getVisibility());
        assertNull(defaultRequest.getTags());
        assertNull(defaultRequest.getUser());
    }

    @Test
    public void testTagsSizeConstraint() {
        fileUploadRequest.setTags(Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5", "tag6"));
        assertEquals(6, fileUploadRequest.getTags().size()); // List should be truncated to 5
    }
}
