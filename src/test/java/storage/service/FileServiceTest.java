package storage.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;
import storage.model.FileUploadRequest;
import storage.model.FileUploadResponse;
import storage.model.enums.Visibility;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @Mock
    private GridFsTemplate gridFsTemplate;

    @Mock
    private GridFSBucket gridFSBucket;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private FileService fileService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStoreFileAlreadyExists() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        GridFSFile gridFSFile = mock(GridFSFile.class);

        FileUploadRequest request = new FileUploadRequest();
        request.setFile(mockFile);
        request.setFilename("test.txt");
        request.setVisibility(Visibility.PUBLIC);
        request.setTags(List.of("tag1", "tag2"));
        request.setUser("user123");

        ObjectId objectId = new ObjectId();
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFSFile);

        FileUploadResponse response = fileService.store(request);

        assertNotNull(response);
        assertNotNull(response.getError());
    }

    @Test
    public void testDeleteFileSuccess() {
        doNothing().when(gridFsTemplate).delete(any(Query.class));
        when(mongoTemplate.remove(any(Query.class), any(String.class))).thenReturn(null);

        boolean result = fileService.deleteFile("test.txt");

        assertTrue(result);
        verify(gridFsTemplate).delete(any(Query.class));
        verify(mongoTemplate).remove(any(Query.class), any(String.class));
    }
}