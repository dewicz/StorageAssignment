# StorageAssignment

This application is designed to manage file uploads and metadata, providing users with a RESTful API for interacting with files stored in a MongoDB database using GridFS.

## Key Features

### 1. File Upload
- Users can upload files with associated metadata, including:
    - Filename
    - Visibility setting (PUBLIC/PRIVATE)
    - Tags (up to 5 tags per file)
- Supports uploading files of any size with a unique filename check to avoid duplicates.

### 2. File Metadata Management
- The application stores and manages metadata such as:
    - Visibility
    - Tags
    - User information
- Users can easily retrieve and update file details.

### 3. Pagination and Sorting
- Fetch lists of files with pagination and sorting options based on various attributes such as:
    - Filename
    - Tags
    - File Size
    - Upload Date

### 4. File Download
- Secure, non-guessable download links are generated for both **public** and **private** files.
- Users can download files directly from the API.

### 5. File Renaming and Metadata Update
- While file content cannot be updated directly, the file metadata (like filename) can be renamed:
    - The process involves retrieving, deleting, and re-uploading the file with updated metadata.

### 6. MongoDB and GridFS
- MongoDB is used to store the metadata.
- GridFS is utilized for efficiently storing large file contents.
- Files are stored and retrieved alongside their associated metadata, enabling users to manage files based on criteria like visibility and more.

## Technologies Used
- **Spring Boot**: Framework for building the RESTful API.
- **Spring Data MongoDB**: For interacting with MongoDB and handling data.
- **MongoDB GridFS**: To handle the storage of large files.

## How it Works
- Files are uploaded via a POST endpoint, where metadata such as visibility and tags are passed along.
- Files can be downloaded using a unique, non-guessable URL generated by the system.
- The system supports pagination and sorting when querying for files, making it easier to retrieve large datasets.
- The metadata of files, including their visibility settings and associated tags, can be modified, with the filename being the most commonly updated attribute.

## Endpoints
Below is a summary if endpoints available. I suggest testing with Postman and providing full file names (test.pdf instead of test) for optimal results.

| **Endpoint**             | **HTTP Method** | **Description**                                                                                                   | **Parameters**                                                                                                                                                                                                                                   |
|--------------------------|----------------|-------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/api/files/upload`      | POST           | Uploads a file along with metadata such as filename, visibility, and tags.                                       | - `filename` (form-data): Name of the file.<br>- `visibility` (form-data): Visibility of the file (`PRIVATE` or `PUBLIC`).<br>- `tags` (form-data): Tags associated with the file.<br>- `file` (form-data): The file to upload.                   |
| `/api/files/download/{fileName}` | GET      | Downloads a file by its name.                                                                                     | - `fileName` (path): Name of the file to download.                                                                                                                                                                                                |
| `/api/files/delete/{fileName}`   | DELETE   | Deletes a file by its name.                                                                                       | - `fileName` (path): Name of the file to delete.                                                                                                                                                                                                  |
| `/api/files/update`      | PUT            | Updates the name of an existing file.                                                                             | - `oldFileName` (query): Current name of the file.<br>- `newFileName` (query): New name for the file.                                                                                                                                              |
| `/api/files/public`      | GET            | Retrieves a paginated list of public files.                                                                       | - `page` (query, default: 1): Page number of the results.                                                                                                                                                                                         |
| `/api/files/byUser/{user}` | GET           | Retrieves a paginated list of files uploaded by a specific user.                                                  | - `user` (path): Username of the uploader.<br>- `page` (query, default: 1): Page number of the results.                                                                                                                                           |
| `/api/files/all`         | GET            | Retrieves a paginated list of all files, with optional sorting and filtering by tags.                             | - `page` (query, default: 1): Page number of the results.<br>- `sortField` (query, optional): Field to sort by (e.g., `tags`, `filename`).<br>- `tags` (query, optional): Tags to filter the files.                                                |


## Project Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/dewicz/storageassignment.git
   cd storageassignment


## Future improvements
While the application meets most of the functional and non-functional requirements, some details were simplified in order to adhere to the time frame suggested for the exercise. Here are some areas for improvement and how I would address them:
### 1. Same file upload validation
Current implementation does not allow to upload a file with the same name, but it does not perform file content checks. To address this, I would compute a hash digest of the file content when it is first stored and store it in the file metadata. When a new file is saved, the digest generated based on its content is compared against the digest values stored in metadata. New file will not be stored in case of a match.
### 2. Filtering and sorting
Current implementation allows sorting by filename, user, size, tags, visibility. Ideally more fields such as content type could be included to allow more granular retrieval.
### 3. Pagination
Page size value is currently set in properties file and user can specify which page to access via request parameter. Depending on requirements, both page size could also be configurable via request parameter
### 4. Testing
Application is tested by unit tests aiming at 80% coverage per class. To improve, testing of more corner cases should be added. Also, integration testing would be a good way to better evaluate database operations and overall application flow 
### 5. Contenerization
Using Docker to contenerize the app would increase application portability and overall deployment workflow. I had issues installing Docker on my machine so decided to skip this part in the interest of timely submission.