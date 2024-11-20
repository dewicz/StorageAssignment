package storage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storage.model.FileUploadRequest;
import storage.service.FileService;
import storage.util.Constants;

import java.io.IOException;


@RestController
@RequestMapping(Constants.FILES_API)
@ComponentScan(basePackages = "storage.service")  // Ensure controller is scanned
public class FileController {

    @Autowired
    FileService fileService;

    @Value("${server.port}")
    private String port;

    @PostMapping(Constants.UPLOAD)
    public ResponseEntity<String> uploadFile(@Valid @ModelAttribute FileUploadRequest fileUploadRequest) {
        try {
            fileService.store(fileUploadRequest);

            String message = String.format("File '%s' uploaded successfully with visibility '%s' and tags %s, follow this link to download it %s",
                    fileUploadRequest.getFilename(), fileUploadRequest.getVisibility(), fileUploadRequest.getTags(),
                    Constants.LOCALHOST + port + Constants.FILES_API + Constants.DOWNLOAD + fileUploadRequest.getFilename());

            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping(Constants.DOWNLOAD_FILE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        InputStreamResource inputStreamResource = fileService.downloadFile(fileName);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .body(inputStreamResource);
    }
}
