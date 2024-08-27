package com.saglobal.connectgd.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/* class to demonstrate use of Drive files list API */
@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "RealEstateImage";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";


    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_FILE);


    private static final String CREDENTIALS_FILE_PATH = "/credentials3.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */

    private final Drive driveService;

    public GoogleDriveService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = new ClassPathResource(CREDENTIALS_FILE_PATH).getInputStream();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String uploadFile(java.io.File filePath, String fileName, String mimeType) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);

        com.google.api.client.http.FileContent mediaContent = new com.google.api.client.http.FileContent(mimeType, filePath);
        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink, webViewLink")
                .execute();

        return file.getWebContentLink();  // Returning the URL to access the file
    }

    public InputStream downloadFile(String fileId) throws IOException {
        File file = driveService.files().get(fileId).execute();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
//    public File downloadFile(String fileId) throws IOException {
//        return driveService.files().get(fileId).execute();
//    }
//    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
//            throws IOException {
//        // Load client secrets.
//        InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//        }
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//        //returns an authorized Credential object.
//        return credential;
//    }
//
//    public Drive getInstance() throws GeneralSecurityException, IOException {
//        // Build a new authorized API client service.
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//        return service;
//    }

//Code needs to be implemented for the uploding a file to drive
//uploading functions are as follows as
//Using this code snippet you can do all drive functionality
//getfiles()
//uploadFile()

//    public  String getfiles() throws IOException, GeneralSecurityException {
//
//        Drive service = getInstance();
//
//        // Print the names and IDs for up to 10 files.
//        FileList result = service.files().list()
//                .setPageSize(10)
//                .execute();
//        List<File> files = result.getFiles();
//        if (files == null || files.isEmpty()) {
//            System.out.println("No files found.");
//            return "No files found.";
//        } else {
//            return files.toString();
//        }
//    }
//
//
//    public String uploadFile(MultipartFile file) {
//        try {
//            System.out.println(file.getOriginalFilename());
//
//            String folderId = "RealEstate";
//            if (file != null) {
//                File fileMetadata = new File();
//                fileMetadata.setParents(Collections.singletonList(folderId));
//                fileMetadata.setName(file.getOriginalFilename());
//                File uploadFile = getInstance()
//                        .files()
//                        .create(fileMetadata, new InputStreamContent(
//                                file.getContentType(),
//                                new ByteArrayInputStream(file.getBytes()))
//                        )
//                        .setFields("id").execute();
//                System.out.println(uploadFile);
//                return uploadFile.getId();
//            }
//        } catch (Exception e) {
//            System.out.printf("Error: "+ e);
//        }
//        return null;
//    }


    //mimeType - jpeg/image
//    public File uploadFile(java.io.File uploadFile, String mimeType) throws IOException, GeneralSecurityException {
//        Drive driveService = getInstance();
//
//        File fileMetadata = new File();
//        fileMetadata.setName(uploadFile.getName());
//
//        FileContent mediaContent = new FileContent(mimeType, uploadFile);
//
//        File file = driveService.files().create(fileMetadata, mediaContent)
//                .setFields("id, webViewLink")
//                .execute();
//
//        return file;
//    }

    // Method to get a list of files from Google Drive
//    public List<File> getFiles() throws IOException, GeneralSecurityException {
//        Drive driveService = getInstance();
//
//        FileList result = driveService.files().list()
//                .setPageSize(10)
//                .setFields("nextPageToken, files(id, name, webViewLink)")
//                .execute();
//
//        return result.getFiles();
//    }


}
