package storage.model;

public class FileUploadResponse {
    String filename;
    String error;

    public FileUploadResponse(String filename, String error) {
        this.filename = filename;
        this.error = error;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
