package storage.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FileUploadResponseTest {

    private FileUploadResponse fileUploadResponse;

    @Before
    public void setUp() {
        fileUploadResponse = new FileUploadResponse("testFile.txt", "File already exists");
    }

    @Test
    public void testGetFilename() {
        assertEquals("testFile.txt", fileUploadResponse.getFilename());
    }

    @Test
    public void testSetFilename() {
        fileUploadResponse.setFilename("newFile.txt");
        assertEquals("newFile.txt", fileUploadResponse.getFilename());
    }

    @Test
    public void testGetError() {
        assertEquals("File already exists", fileUploadResponse.getError());
    }

    @Test
    public void testSetError() {
        fileUploadResponse.setError("File upload failed");
        assertEquals("File upload failed", fileUploadResponse.getError());
    }

    @Test
    public void testDefaultConstructor() {
        FileUploadResponse defaultResponse = new FileUploadResponse();
        assertNull(defaultResponse.getFilename());
        assertNull(defaultResponse.getError());
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals("testFile.txt", fileUploadResponse.getFilename());
        assertEquals("File already exists", fileUploadResponse.getError());
    }
}
