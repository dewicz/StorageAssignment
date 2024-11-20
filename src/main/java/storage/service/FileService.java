package storage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    private final Path rootLocation = Paths.get("uploaded-files");

    public void store(MultipartFile file, String filename) throws IOException {
        Files.createDirectories(rootLocation);
        Path destinationFile = rootLocation.resolve(Paths.get(filename))
                .normalize().toAbsolutePath();

        if (!file.isEmpty()) {
            file.transferTo(destinationFile);
        } else {
            throw new IOException("Failed to store file " + filename);
        }
    }
}
