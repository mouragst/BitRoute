package com.moura.bitroute.service;

import com.moura.bitroute.config.StorageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
public class StorageService {
    
    private final StorageConfig storageConfig;
    
    public String storePaste(String shortlink, String content) {
        try {
            Path storagePath = Paths.get(storageConfig.getPath());
            
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }
            
            Path filePath = storagePath.resolve(shortlink + ".txt");
            
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            return filePath.toString();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store paste content for shortlink: " + shortlink, e);
        }
    }
    
    public String retrievePaste(String pastePath) {
        try {
            Path filePath = Paths.get(pastePath);
            
            if (!Files.exists(filePath)) {
                throw new RuntimeException("Paste file not found: " + pastePath);
            }
            
            return Files.readString(filePath);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve paste content from: " + pastePath, e);
        }
    }
    
    public void deletePaste(String pastePath) {
        try {
            Path filePath = Paths.get(pastePath);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete paste content from: " + pastePath, e);
        }
    }
}
