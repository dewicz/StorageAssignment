package storage.model;

import storage.model.enums.Visibility;

import java.util.List;

public class FileListResponse {
    private String filename;
    private Visibility visibility;
    private List<String> tags;
    private String user;
    private String downloadLink;

    public FileListResponse(String filename, Visibility visibility, List<String> tags, String user, String downloadLink) {
        this.filename = filename;
        this.visibility = visibility;
        this.tags = tags;
        this.user = user;
        this.downloadLink = downloadLink;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
