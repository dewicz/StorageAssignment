package storage.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import storage.model.FileMetadata;
import storage.model.FileUploadRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private MongoTemplate mongoTemplate;

    public String store(FileUploadRequest request) throws IOException {
        MultipartFile file = request.getFile();
        try {
            String fileId = gridFsTemplate.store(file.getInputStream(), request.getFilename(), file.getContentType()).toString();

            FileMetadata metadata = new FileMetadata();
            metadata.setFileId(fileId);
            metadata.setFilename(request.getFilename());
            metadata.setVisibility(request.getVisibility());
            metadata.setTags(request.getTags());
            metadata.setUser(request.getUser());

            mongoTemplate.save(metadata);

            return fileId.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStreamResource downloadFile(String fileName) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(fileName)));
        if (gridFSFile != null) {
            InputStream fileStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

            return new InputStreamResource(fileStream);
        }
        return null;
    }
}
