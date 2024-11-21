package storage.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import storage.model.FileDownloadResponse;
import storage.model.FileListResponse;
import storage.model.FileUploadRequest;
import storage.model.FileUploadResponse;
import storage.model.enums.Visibility;
import storage.service.FileService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    public void testUploadFileSuccess() throws Exception {
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFilename("testfile.pdf");
        fileUploadRequest.setVisibility(Visibility.PUBLIC);
        fileUploadRequest.setTags(Arrays.asList("tag1", "tag2"));
        fileUploadRequest.setUser("user");

        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setFilename("testfile.pdf");

        when(fileService.store(any())).thenReturn(fileUploadResponse);

        mockMvc.perform(multipart("/api/files/upload")
                        .file("file", "file content".getBytes())
                        .param("filename", "testfile.pdf")
                        .param("visibility", "PUBLIC")
                        .param("user", "user")
                        .param("tags", "tag1", "tag2"))
                .andExpect(status().isOk())
                .andExpect(content().string("File 'testfile.pdf' uploaded successfully with visibility 'PUBLIC' and tags [tag1, tag2], follow this link to download it http://localhost:null/api/files/download/testfile.pdf"));
    }

    @Test
    public void testDownloadFile() throws Exception {
        String filename = "testfile.pdf";

        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        fileDownloadResponse.setContentType("application/pdf");

        when(fileService.downloadFile(filename)).thenReturn(fileDownloadResponse);
        mockMvc.perform(get("/api/files/download/testfile.pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=" + filename)); // Mock file content bytes
    }

    @Test
    public void testDeleteFile() throws Exception {
        String filename = "testfile.pdf";

        when(fileService.deleteFile(filename)).thenReturn(true);
        mockMvc.perform(delete("/api/files/delete/testfile.pdf"));
    }

    @Test
    public void testUpdateFileNameSuccess() throws Exception {
        String oldFileName = "oldFile.txt";
        String newFileName = "newFile.txt";

        when(fileService.updateFileName(oldFileName, newFileName)).thenReturn(true);

        mockMvc.perform(put("/api/files/update")
                        .param("oldFileName", oldFileName)
                        .param("newFileName", newFileName))
                .andExpect(status().isOk())
                .andExpect(content().string("File name updated successfully."));
    }

    @Test
    public void testUpdateFileNameFailure() throws Exception {
        String oldFileName = "oldFile.txt";
        String newFileName = "newFile.txt";

        when(fileService.updateFileName(oldFileName, newFileName)).thenReturn(false);

        mockMvc.perform(put("/api/files/update")
                        .param("oldFileName", oldFileName)
                        .param("newFileName", newFileName))
                .andExpect(status().isNotFound())
                .andExpect(content().string("File not found or update failed."));
    }

    @Test
    public void testListFiles() throws Exception {
        int page = 1;
        List<FileListResponse> fileListResponses = Arrays.asList(
                new FileListResponse("file1.txt", Visibility.PRIVATE, Arrays.asList("tag1"), "user", 1L, "link"),
                new FileListResponse("file2.txt", Visibility.PUBLIC, Arrays.asList("tag2"), "user", 1L, "link")
        );
        long totalFiles = 2;
        List<String> tags = Arrays.asList("tag1");

        when(fileService.countFiles(tags)).thenReturn(totalFiles);
        when(fileService.getAggregatedTags()).thenReturn(Set.of("tag1", "tag2", "tag3"));

        mockMvc.perform(get("/api/files/all")
                        .param("page", String.valueOf(page))
                        .param("tags", "tag1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(totalFiles));
    }

    @Test
    public void testListPublicFiles() throws Exception {
        int page = 1;
        List<FileListResponse> fileListResponses = Arrays.asList(
                new FileListResponse("file1.txt", Visibility.PUBLIC, Arrays.asList("tag1"), "user", 1L, "link"),
                new FileListResponse("file2.txt", Visibility.PUBLIC, Arrays.asList("tag2"), "user", 1L, "link")
        );
        long totalFiles = 2;

        when(fileService.countPublicFiles()).thenReturn(totalFiles);
        when(fileService.listPublicFiles(any(), any())).thenReturn(fileListResponses);

        mockMvc.perform(get("/api/files/public")
                        .param("tags", "tag1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(totalFiles))
                .andExpect(jsonPath("$.files.[0].visibility").value("PUBLIC"));
    }

    @Test
    public void testListUserFiles() throws Exception {
        int page = 1;
        List<FileListResponse> fileListResponses = Arrays.asList(
                new FileListResponse("file1.txt", Visibility.PUBLIC, Arrays.asList("tag1"), "user", 1L, "link"),
                new FileListResponse("file2.txt", Visibility.PUBLIC, Arrays.asList("tag2"), "user", 1L, "link")
        );
        long totalFiles = 2;

        when(fileService.countUserFiles("user")).thenReturn(totalFiles);
        when(fileService.listUserFiles(any(), any(), any())).thenReturn(fileListResponses);

        mockMvc.perform(get("/api/files/byUser/user")
                        .param("tags", "tag1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(totalFiles))
                .andExpect(jsonPath("$.files.[0].user").value("user"));
    }
}

