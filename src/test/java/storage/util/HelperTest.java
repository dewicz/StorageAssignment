package storage.util;

import org.junit.Before;
import org.junit.Test;
import storage.model.FileListResponse;
import storage.model.enums.Visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HelperTest {

    @Before
    public void setUp() {
        Helper.staticServerPort = "8080";
    }

    @Test
    public void testConstructDownloadLink() {
        String filename = "testfile.pdf";
        String expectedLink = "http://localhost:8080/api/files/download/testfile.pdf";
        String actualLink = Helper.constructDownloadLink(filename);
        assertEquals("The download link should match the expected value.", expectedLink, actualLink);
    }

    @Test
    public void testTagSortConverter() {
        String expectedSortField = "tags.0";
        String actualSortField = Helper.tagSortConverter();
        assertEquals("The tag sort converter should return 'tags.0'.", expectedSortField, actualSortField);
    }

    @Test
    public void testConstructResponse() {
        List<FileListResponse> files = new ArrayList<>();
        files.add(new FileListResponse("testfile.pdf", Visibility.PRIVATE, List.of("tag1", "tag2"), "user", 1L, "link"));
        int page = 1;
        long totalFiles = 15;
        int size = 5;
        int totalPages = 3;

        Map<String, Object> response = Helper.constructResponse(files, page, totalFiles, size);

        assertEquals("The files list should match.", files, response.get("files"));
        assertEquals("The current page should match.", page, response.get("currentPage"));
        assertEquals("The total items should match.", totalFiles, response.get("totalItems"));
        assertEquals("The total pages should match.", totalPages, response.get("totalPages"));
    }
}