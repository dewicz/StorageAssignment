package storage.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    @Value("${server.port}")
    private String serverPort;

    private static String staticServerPort;

    @PostConstruct
    private void init() {
        staticServerPort = serverPort;
    }

    public static String constructDownloadLink(String filename) {
        return Constants.LOCALHOST + staticServerPort + Constants.FILES_API + Constants.DOWNLOAD + "/" + filename;
    }

    public static String tagSortConverter(String tag) {
        return "tags.0";
    }
}
