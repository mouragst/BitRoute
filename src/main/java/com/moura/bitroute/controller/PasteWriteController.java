package com.moura.bitroute.controller;

import com.moura.bitroute.dto.CreatePasteRequest;
import com.moura.bitroute.dto.CreatePasteResponse;
import com.moura.bitroute.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/paste")
@RequiredArgsConstructor
public class PasteWriteController {
    
    private final PasteService pasteService;
    
    @PostMapping
    public ResponseEntity<CreatePasteResponse> createPaste(
            @RequestBody CreatePasteRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIpAddress(httpRequest);
        
        CreatePasteResponse response = pasteService.createPaste(request, ipAddress);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
