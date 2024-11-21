package storage.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import storage.model.*;
import storage.model.enums.Visibility;
import storage.util.Constants;
import storage.util.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Set<String> aggregatedTags;

    public FileUploadResponse store(FileUploadRequest request) throws IOException {
        MultipartFile file = request.getFile();
        GridFSFile existingFile = gridFsTemplate.findOne(new Query(Criteria.where("filename").is(request.getFilename())));

        if (existingFile != null) {
            return new FileUploadResponse(null, "File with the same name already exists");
        }
        try {
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), request.getFilename(), file.getContentType());

            // Retrieve GridFSFile using the ObjectId
            GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));

            // Get file size from GridFSFile
            long size = gridFSFile.getLength();
            FileMetadata metadata = new FileMetadata();
            metadata.setFileId(fileId.toString());
            metadata.setFilename(request.getFilename());
            metadata.setVisibility(request.getVisibility());
            metadata.setTags(request.getTags());
            metadata.setUser(request.getUser());
            metadata.setSize(size);

            mongoTemplate.save(metadata);

            return new FileUploadResponse(fileId.toString(), null);
        } catch (IOException e) {
            e.printStackTrace();
            return new FileUploadResponse(null, "Error presisting file");
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

    public boolean updateFileName(String oldFileName, String newFileName) {
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
            Query queryFileId = new Query(Criteria.where("filename").is(newFileName));
            mongoTemplate.updateFirst(queryFileId, updateFileId, Constants.COLLECTION_NAME);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<FileListResponse> listPublicFiles(Integer page, Integer size) {
        Query query = new Query(Criteria.where("visibility").is(Visibility.PUBLIC));
        query.skip((page-1) * size).limit(size);

        return constructListResponse(mongoTemplate.find(query, FileMetadata.class, Constants.COLLECTION_NAME));
    }

    public long countPublicFiles() {
        Query query = new Query()
                .addCriteria(Criteria.where("visibility").is(Visibility.PUBLIC));

        return mongoTemplate.count(query, FileMetadata.class, Constants.COLLECTION_NAME);
    }

    public List<FileListResponse> listUserFiles(String user, Integer page, Integer size) {
        Query query = new Query(Criteria.where("user").is(user));
        query.skip((page-1) * size).limit(size);

        return constructListResponse(mongoTemplate.find(query, FileMetadata.class, Constants.COLLECTION_NAME));
    }

    public long countUserFiles(String user) {
        Query query = new Query()
                .addCriteria(Criteria.where("user").is(user));

        return mongoTemplate.count(query, FileMetadata.class, Constants.COLLECTION_NAME);
    }

    public List<FileListResponse> listFiles(Integer page, Integer size, Sort sort, List<String> tags) {
        Query query = new Query();
        query.skip((page-1) * size).limit(size).with(sort)
                .collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        if(!tags.isEmpty()) {
            query.addCriteria(Criteria.where("tags").in(tags));
        }
        return constructListResponse(mongoTemplate.find(query, FileMetadata.class, Constants.COLLECTION_NAME));
    }

    public long countFiles(List<String> tags) {
        Query query = new Query();
        if(!tags.isEmpty()) {
            query.addCriteria(Criteria.where("tags").in(tags))
                    .collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        }
        return mongoTemplate.count(query, FileMetadata.class, Constants.COLLECTION_NAME);
    }

    private List<FileListResponse> constructListResponse(List<FileMetadata> files) {
        return files.stream()
                .map(file -> new FileListResponse(
                        file.getFilename(),
                        file.getVisibility(),
                        file.getTags(),
                        file.getUser(),
                        file.getSize(),
                        Helper.constructDownloadLink(file.getFilename())
                ))
                .collect(Collectors.toList());
    }

    public Set<String> getAggregatedTags() {
        if(aggregatedTags == null) {
            aggregatedTags = new HashSet<>();
            List<FileMetadata> files = mongoTemplate.find(new Query(), FileMetadata.class, Constants.COLLECTION_NAME);
            files.forEach(file -> {
                aggregatedTags.addAll(file.getTags());
            });
        }
        return aggregatedTags;
    }
}
