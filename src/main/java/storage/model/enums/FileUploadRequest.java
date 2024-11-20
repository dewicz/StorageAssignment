package storage.model.enums;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileUploadRequest {
    @NotNull
    private MultipartFile file;

    @NotNull
    private String filename;

    @NotNull
    private Visibility visibility;

    @Size(max = 5)
    private List<String> tags;

    public @NotNull MultipartFile getFile() {
        return file;
    }

    public void setFile(@NotNull MultipartFile file) {
        this.file = file;
    }

    public @NotNull String getFilename() {
        return filename;
    }

    public void setFilename(@NotNull String filename) {
        this.filename = filename;
    }

    public @NotNull Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(@NotNull Visibility visibility) {
        this.visibility = visibility;
    }

    public @Size(max = 5) List<String> getTags() {
        return tags;
    }

    public void setTags(@Size(max = 5) List<String> tags) {
        this.tags = tags;
    }
}
