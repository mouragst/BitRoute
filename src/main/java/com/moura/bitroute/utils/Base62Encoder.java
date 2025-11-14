package com.moura.bitroute.utils;

import com.moura.bitroute.exception.ShortlinkGenerationException;
import com.moura.bitroute.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORTLINK_LENGTH = 7;
    private static final int MAX_RETRIES = 10;
    
    private final PasteRepository pasteRepository;
    
    public String generateShortLink() {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String shortLink = generateFromUUID();
            
            if (!pasteRepository.existsByShortlink(shortLink)) {
                return shortLink;
            }
        }
        
        throw new ShortlinkGenerationException("Failed to generate unique shortlink after " + MAX_RETRIES + " attempts");
    }
    
    private String generateFromUUID() {
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        
        long combined = Math.abs(mostSignificantBits ^ leastSignificantBits);
        
        String base62 = encodeBase62(combined);
        
        return base62.length() >= SHORTLINK_LENGTH 
            ? base62.substring(0, SHORTLINK_LENGTH)
            : padLeft(base62, SHORTLINK_LENGTH);
    }
    
    private String encodeBase62(long number) {
        if (number == 0) {
            return "0";
        }
        
        StringBuilder result = new StringBuilder();
        
        while (number > 0) {
            result.insert(0, BASE62_CHARS.charAt((int) (number % 62)));
            number /= 62;
        }
        
        return result.toString();
    }
    
    private String padLeft(String str, int length) {
        while (str.length() < length) {
            str = "0" + str;
        }
        return str;
    }
}