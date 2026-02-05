package com.moura.bitroute.service;

import com.moura.bitroute.dto.CreatePasteRequest;
import com.moura.bitroute.dto.CreatePasteResponse;
import com.moura.bitroute.dto.PasteViewEvent;
import com.moura.bitroute.dto.ViewPasteResponse;
import com.moura.bitroute.exception.PasteExpiredException;
import com.moura.bitroute.exception.PasteNotFoundException;
import com.moura.bitroute.messaging.AnalyticsProducer;
import com.moura.bitroute.model.Paste;
import com.moura.bitroute.repository.PasteRepository;
import com.moura.bitroute.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasteService {
    
    private final PasteRepository pasteRepository;
    private final Base62Encoder base62Encoder;
    private final StorageService storageService;
    private final AnalyticsProducer analyticsProducer;
    
    @Transactional
    public CreatePasteResponse createPaste(CreatePasteRequest request, String ipAddress) {
        String shortlink = base62Encoder.generateShortLink(ipAddress);
        
        String pastePath = storageService.storePaste(shortlink, request.getPasteContent());
        
        Paste paste = Paste.builder()
                .shortlink(shortlink)
                .expirationLengthInMinutes(request.getExpirationLengthInMinutes())
                .createdAt(LocalDateTime.now())
                .pastePath(pastePath)
                .build();
        
        pasteRepository.save(paste);
        
        return CreatePasteResponse.builder()
                .shortlink(shortlink)
                .build();
    }
    
    @Transactional(readOnly = true)
    public ViewPasteResponse viewPaste(String shortlink) {
        Paste paste = pasteRepository.findById(shortlink)
                .orElseThrow(() -> new PasteNotFoundException(shortlink));
        
        if (paste.isExpired()) {
            throw new PasteExpiredException(shortlink);
        }
        
        String pasteContent = storageService.retrievePaste(paste.getPastePath());
        
        publishViewEvent(shortlink);
        
        return ViewPasteResponse.builder()
                .pasteContent(pasteContent)
                .createdAt(paste.getCreatedAt())
                .expirationLengthInMinutes(paste.getExpirationLengthInMinutes())
                .build();
    }
    
    private void publishViewEvent(String shortlink) {
        PasteViewEvent event = PasteViewEvent.builder()
                .shortlink(shortlink)
                .viewedAt(LocalDateTime.now())
                .build();
        
        analyticsProducer.publishViewEvent(event);
    }
}
