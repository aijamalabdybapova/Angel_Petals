package com.flowershop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads")
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось создать директорию для загрузки файлов", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("Файл пуст");
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Разрешены только изображения");
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось сохранить файл", ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось удалить файл: " + fileName, ex);
        }
    }

    public Path loadFile(String fileName) {
        return fileStorageLocation.resolve(fileName).normalize();
    }
}