package storage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storage.model.FileDownloadResponse;
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
        FileDownloadResponse fileDownloadResponse = fileService.downloadFile(fileName);
        return ResponseEntity.ok()
                .header("Content-Type", fileDownloadResponse.getContentType())
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .body(fileDownloadResponse.getInputStreamResource());
    }

    @DeleteMapping(Constants.DELETE_FILE)
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            boolean isDeleted = fileService.deleteFile(fileName);
            if (isDeleted) {
                return ResponseEntity.ok("File deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateFileName(@RequestParam String oldFileName,
                                                 @RequestParam String newFileName) {
        boolean isUpdated = fileService.updateFileNameByFilename(oldFileName, newFileName);

        if (isUpdated) {
            return ResponseEntity.ok("File name updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found or update failed.");
        }
    }
}
