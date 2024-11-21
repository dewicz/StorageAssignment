package storage.model;

import org.springframework.core.io.InputStreamResource;

public class FileDownloadResponse {
    InputStreamResource inputStreamResource;
    String contentType;

    public FileDownloadResponse(InputStreamResource inputStreamResource, String contentType) {
        this.inputStreamResource = inputStreamResource;
        this.contentType = contentType;
    }

    public FileDownloadResponse() {
    }

    public InputStreamResource getInputStreamResource() {
        return inputStreamResource;
    }

    public void setInputStreamResource(InputStreamResource inputStreamResource) {
        this.inputStreamResource = inputStreamResource;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
