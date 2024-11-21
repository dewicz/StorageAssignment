package storage.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;

public class FileDownloadResponseTest {

    private FileDownloadResponse fileDownloadResponse;
    private InputStreamResource inputStreamResource;
    private String contentType;

    @Before
    public void setUp() {
        contentType = "application/pdf";
        inputStreamResource = new InputStreamResource(new ByteArrayInputStream("test content".getBytes()));
        fileDownloadResponse = new FileDownloadResponse(inputStreamResource, contentType);
    }

    @Test
    public void testConstructor() {
        assertNotNull(fileDownloadResponse.getInputStreamResource());
        assertEquals(contentType, fileDownloadResponse.getContentType());
    }

    @Test
    public void testGetInputStreamResource() {
        assertEquals(inputStreamResource, fileDownloadResponse.getInputStreamResource());
    }

    @Test
    public void testSetInputStreamResource() {
        InputStreamResource newResource = Mockito.mock(InputStreamResource.class);
        fileDownloadResponse.setInputStreamResource(newResource);
        assertEquals(newResource, fileDownloadResponse.getInputStreamResource());
    }

    @Test
    public void testGetContentType() {
        assertEquals(contentType, fileDownloadResponse.getContentType());
    }

    @Test
    public void testSetContentType() {
        String newContentType = "image/png";
        fileDownloadResponse.setContentType(newContentType);
        assertEquals(newContentType, fileDownloadResponse.getContentType());
    }

    @Test
    public void testDefaultConstructor() {
        FileDownloadResponse defaultResponse = new FileDownloadResponse();
        assertNull(defaultResponse.getInputStreamResource());
        assertNull(defaultResponse.getContentType());
    }
}
