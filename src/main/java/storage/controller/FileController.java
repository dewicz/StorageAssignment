package storage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storage.model.enums.FileUploadRequest;
import storage.service.FileService;

import java.io.IOException;


@RestController
@RequestMapping("/api/files")
@ComponentScan(basePackages = "storage.service")  // Ensure controller is scanned
public class FileController {

    @Autowired
    FileService fileService;


    @GetMapping("/test")
    public String homePage() {
        return "home";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@Valid @ModelAttribute FileUploadRequest fileUploadRequest) {
        try {
            // Save the file
            fileService.store(fileUploadRequest.getFile(), fileUploadRequest.getFilename());

            // Process other metadata like visibility and tags if needed
            String message = String.format("File '%s' uploaded successfully with visibility '%s' and tags %s",
                    fileUploadRequest.getFilename(), fileUploadRequest.getVisibility(), fileUploadRequest.getTags());

            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }


    //upload file name
    //change file name
    //list files stored
}
