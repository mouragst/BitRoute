package com.moura.bitroute.controller;

import com.moura.bitroute.dto.ViewPasteResponse;
import com.moura.bitroute.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/paste")
@RequiredArgsConstructor
public class PasteReadController {
    
    private final PasteService pasteService;
    
    @GetMapping
    public ResponseEntity<ViewPasteResponse> viewPaste(
            @RequestParam String shortlink,
            HttpServletRequest request) {
        
        ViewPasteResponse response = pasteService.viewPaste(shortlink);
        return ResponseEntity.ok(response);
    }
}
