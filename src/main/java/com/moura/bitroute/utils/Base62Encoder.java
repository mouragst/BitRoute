package com.moura.bitroute.utils;

import com.moura.bitroute.exception.ShortlinkGenerationException;
import com.moura.bitroute.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORTLINK_LENGTH = 7;
    private static final int MAX_RETRIES = 10;
    
    private final PasteRepository pasteRepository;
    
    public String generateShortLink(String ipAddress) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String shortLink = generateFromMD5(ipAddress);
            
            if (!pasteRepository.existsByShortlink(shortLink)) {
                return shortLink;
            }
        }
        
        throw new ShortlinkGenerationException("Failed to generate unique shortlink after " + MAX_RETRIES + " attempts");
    }
    
    private String generateFromMD5(String ipAddress) {
        try {
            String input = ipAddress + System.currentTimeMillis();
            
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md5.digest(input.getBytes(StandardCharsets.UTF_8));
            
            BigInteger hashNumber = new BigInteger(1, hashBytes);
            
            String base62 = encodeBase62(hashNumber);
            
            return base62.length() >= SHORTLINK_LENGTH 
                ? base62.substring(0, SHORTLINK_LENGTH)
                : padLeft(base62);
                
        } catch (NoSuchAlgorithmException e) {
            throw new ShortlinkGenerationException("MD5 algorithm not available", e);
        }
    }
    
    private String encodeBase62(BigInteger number) {
        if (number.equals(BigInteger.ZERO)) {
            return "0";
        }
        
        StringBuilder result = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divMod = number.divideAndRemainder(base);
            result.insert(0, BASE62_CHARS.charAt(divMod[1].intValue()));
            number = divMod[0];
        }
        
        return result.toString();
    }
   
    private String padLeft(String str) {
        while (str.length() < SHORTLINK_LENGTH) {
            str = "0" + str;
        }
        return str;
    }
}