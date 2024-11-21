package storage.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import storage.model.FileListResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class Helper {

    @Value("${server.port}")
    private String serverPort;

    public static String staticServerPort;

    @PostConstruct
    private void init() {
        staticServerPort = serverPort;
    }

    public static String constructDownloadLink(String filename) {
        return Constants.LOCALHOST + staticServerPort + Constants.FILES_API + Constants.DOWNLOAD + "/" + filename;
    }

    public static String tagSortConverter() {
        return "tags.0";
    }

    public static Map<String, Object> constructResponse(List<FileListResponse> files, int page, long totalFiles, int size) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("files", files);
        response.put("currentPage", page);
        response.put("totalItems", totalFiles);
        response.put("totalPages", (int) Math.ceil((double) totalFiles / size));
        return response;
    }
}
