package com.saglobal.connectgd.controller;

import com.saglobal.connectgd.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/google-drive")
public class REController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Convert MultipartFile to a java.io.File
            File convFile = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            String fileUrl = googleDriveService.uploadFile(convFile, file.getOriginalFilename(), file.getContentType());

            String jsonResponse = "{\"url\":\"" + fileUrl + "\"}";
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

//    @GetMapping("/download/{fileId}")
//    public ResponseEntity<String> downloadFile(@PathVariable String fileId) {
//        try {
//            googleDriveService.downloadFile(fileId);
//            return ResponseEntity.ok("File downloaded successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file: " + e.getMessage());
//        }
//    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        try {
            InputStream inputStream = googleDriveService.downloadFile(fileId);


            String mimeType = "image/jpeg";
            String fileName = "downloaded-file";

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}





//@Component
//public class REController {
//
//    @Autowired
//    private GoogleDriveService service;
//
//    public String getAllPictures() throws IOException, GeneralSecurityException{
//        return service.getfiles();
//    }
//    public String uploadPicture(MultipartFile file) throws IOException, GeneralSecurityException{
//        System.out.println(file.getOriginalFilename());
//
//        return service.uploadFile(file);
//    }
//
//}
