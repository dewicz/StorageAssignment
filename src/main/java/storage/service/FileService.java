package storage.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import storage.model.FileDownloadResponse;
import storage.model.FileListResponse;
import storage.model.FileMetadata;
import storage.model.FileUploadRequest;
import storage.model.enums.Visibility;
import storage.util.Constants;
import storage.util.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

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

    public FileDownloadResponse downloadFile(String fileName) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(fileName)));
        if (gridFSFile != null) {
            String contentType = gridFSFile.getMetadata() != null
                    ? gridFSFile.getMetadata().getString("_contentType")
                    : "application/octet-stream";
            InputStream fileStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            return new FileDownloadResponse(new InputStreamResource(fileStream), contentType);
        }
        return null;
    }

    public boolean deleteFile(String fileName) {
        try {
            gridFsTemplate.delete(new Query(Criteria.where("filename").is(fileName)));
            mongoTemplate.remove(new Query(Criteria.where("filename").is(fileName)), Constants.COLLECTION_NAME);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFileNameByFilename(String oldFileName, String newFileName) {
        try {
            Query query = new Query(Criteria.where("filename").is(oldFileName));
            GridFSFile gridFSFile = gridFsTemplate.findOne(query);

            if (gridFSFile == null) {
                return false; //file not found
            }

            InputStream fileContent = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(gridFSFile.getMetadata());

            // Re-upload the file with the new filename
            String newFileId = gridFsTemplate.store(fileContent, newFileName, gridFSFile.getMetadata() != null
                    ? gridFSFile.getMetadata().getString("_contentType")
                    : "application/octet-stream", options.getMetadata()).toString();
            gridFsTemplate.delete(query);

            Update updateFilename = new Update().set("filename", newFileName);
            Update updateFileId = new Update().set("fileId", newFileId);
            mongoTemplate.updateFirst(query, updateFilename, Constants.COLLECTION_NAME);
            mongoTemplate.updateFirst(query, updateFileId, Constants.COLLECTION_NAME);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FileListResponse> listPublicFiles(int page, int size) {
        Query query = new Query(Criteria.where("visibility").is(Visibility.PUBLIC));
        query.skip((page-1) * size).limit(size);

        List<FileMetadata> files = mongoTemplate.find(query, FileMetadata.class, Constants.COLLECTION_NAME);

        return files.stream()
                .map(file -> new FileListResponse(
                        file.getFilename(),
                        file.getVisibility(),
                        file.getTags(),
                        file.getUser(),
                        Helper.constructDownloadLink(file.getFilename())
                ))
                .collect(Collectors.toList());
    }

    public long countPublicFiles() {
        Query query = new Query()
                .addCriteria(Criteria.where("visibility").is(Visibility.PUBLIC));

        return mongoTemplate.count(query, FileMetadata.class, Constants.COLLECTION_NAME);
    }

    public List<FileListResponse> listUserFiles(String user, int page, int size) {
        Query query = new Query(Criteria.where("user").is(user));
        query.skip((page-1) * size).limit(size);

        List<FileMetadata> files = mongoTemplate.find(query, FileMetadata.class, Constants.COLLECTION_NAME);

        return files.stream()
                .map(file -> new FileListResponse(
                        file.getFilename(),
                        file.getVisibility(),
                        file.getTags(),
                        file.getUser(),
                        Helper.constructDownloadLink(file.getFilename())
                ))
                .collect(Collectors.toList());
    }

    public long countUserFiles(String user) {
        Query query = new Query()
                .addCriteria(Criteria.where("user").is(user));

        return mongoTemplate.count(query, FileMetadata.class, Constants.COLLECTION_NAME);
    }
}
