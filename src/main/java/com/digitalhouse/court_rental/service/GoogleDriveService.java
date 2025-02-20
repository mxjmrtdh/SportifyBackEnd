package com.digitalhouse.court_rental.service;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "CourtRental";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/credentials.json";
    private static final String FOLDER_ID = "1yemzSqpSul2lBqyA1M0uqFfY-muBo1uH";

    private Drive getDriveService() throws IOException {
        InputStream inputStream = new FileInputStream(SERVICE_ACCOUNT_KEY_PATH);
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(new NetHttpTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        Drive driveService = getDriveService();

        java.io.File tempFile = java.io.File.createTempFile("temp", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(FOLDER_ID));

        FileContent mediaContent = new FileContent(multipartFile.getContentType(), tempFile);
        com.google.api.services.drive.model.File uploadedFile = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        tempFile.delete();

        makeFilePublic(uploadedFile.getId(), driveService);

        return "https://drive.google.com/uc?id=" + uploadedFile.getId();
    }

    private void makeFilePublic(String fileId, Drive driveService) throws IOException {
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");

        driveService.permissions().create(fileId, permission).execute();
    }
}
