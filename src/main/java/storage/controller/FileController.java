package storage.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storage.model.FileDownloadResponse;
import storage.model.FileListResponse;
import storage.model.FileUploadRequest;
import storage.model.FileUploadResponse;
import storage.service.FileService;
import storage.util.Constants;
import storage.util.Helper;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping(Constants.FILES_API)
@ComponentScan(basePackages = "storage.service")  // Ensure controller is scanned
public class FileController {

    @Autowired
    FileService fileService;

    @Value("${page.size}")
    private int size;

    @PostMapping(Constants.UPLOAD)
    public ResponseEntity<String> uploadFile(@Valid @ModelAttribute FileUploadRequest fileUploadRequest) {
        try {
            FileUploadResponse response = fileService.store(fileUploadRequest);

            if(response.getFilename() != null) {
                String message = String.format("File '%s' uploaded successfully with visibility '%s' and tags %s, follow this link to download it %s",
                        fileUploadRequest.getFilename(), fileUploadRequest.getVisibility(), fileUploadRequest.getTags(),
                        Helper.constructDownloadLink(fileUploadRequest.getFilename()));

                return ResponseEntity.ok(message);
            }
            return ResponseEntity.status(500).body("Failed to upload file: " + response.getError());
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

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> listPublicFiles(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<FileListResponse> publicFiles = fileService.listPublicFiles(page, size);
        long totalFiles = fileService.countPublicFiles();
        response.put("files", publicFiles);
        response.put("currentPage", page);
        response.put("totalItems", totalFiles);
        response.put("totalPages", (int) Math.ceil((double) totalFiles / size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byUser/{user}")
    public ResponseEntity<Map<String, Object>> listUserFiles(@PathVariable String user, @RequestParam(defaultValue = "1") int page) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<FileListResponse> publicFiles = fileService.listUserFiles(user,page, size);
        long totalFiles = fileService.countUserFiles(user);
        response.put("files", publicFiles);
        response.put("currentPage", page);
        response.put("totalItems", totalFiles);
        response.put("totalPages", (int) Math.ceil((double) totalFiles / size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> listFiles(@RequestParam(defaultValue = "1") int page, @RequestParam Optional<String> sortField) {
        Map<String, Object> response = new LinkedHashMap<>();

        Sort sort = Sort.unsorted();
        if(sortField.isPresent()) {
            String sortFieldValue = sortField.get();
            if(sortFieldValue.equalsIgnoreCase("tags")) {
                sortFieldValue = Helper.tagSortConverter(sortFieldValue);
            }
            sort = Sort.by(Sort.Order.asc(sortFieldValue));
        }

        List<FileListResponse> publicFiles = fileService.listFiles(page, size, sort);
        long totalFiles = fileService.countFiles();
        response.put("files", publicFiles);
        response.put("currentPage", page);
        response.put("totalItems", totalFiles);
        response.put("totalPages", (int) Math.ceil((double) totalFiles / size));
        return ResponseEntity.ok(response);
    }
}
