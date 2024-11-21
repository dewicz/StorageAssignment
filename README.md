# StorageAssignment

This application is designed to manage file uploads and metadata, providing users with a RESTful API for interacting with files stored in a MongoDB database using GridFS. Key features include:

File Upload: Users can upload files with associated metadata, including a filename, visibility setting (PUBLIC/PRIVATE), and tags (up to 5 tags per file). Files of any size can be uploaded, with a unique filename check to avoid duplicates.

File Metadata Management: The application stores and manages metadata such as visibility, tags, and user information, ensuring users can retrieve and update file details easily.

Pagination and Sorting: Users can fetch lists of files with pagination and sorting based on various attributes like filename, tags, and file size.

File Download: The application provides secure, non-guessable download links for both public and private files. Users can also delete files they have uploaded.

File Renaming and Metadata Update: While the file content cannot be updated directly, the file metadata (like filename) can be renamed by retrieving, deleting, and re-uploading the file with the updated details.

MongoDB and GridFS: MongoDB is used for storing the metadata, and GridFS is used for storing large file contents efficiently. The files are stored and retrieved with their associated metadata, and users can manage files based on visibility and other criteria.

This service is built with Spring Boot, Spring Data MongoDB, and MongoDB GridFS to provide a robust solution for managing large files with custom metadata, using best practices in RESTful API design.



