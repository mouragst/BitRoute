package com.moura.bitroute.jobs;

import com.moura.bitroute.model.Paste;
import com.moura.bitroute.repository.PasteAnalyticsRepository;
import com.moura.bitroute.repository.PasteRepository;
import com.moura.bitroute.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredPastesCleaner {
    
    private final PasteRepository pasteRepository;
    private final PasteAnalyticsRepository analyticsRepository;
    private final StorageService storageService;
    
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanExpiredPastes() {
        try {
            List<Paste> expiredPastes = pasteRepository.findExpiredPastes(LocalDateTime.now());
            
            if (expiredPastes.isEmpty()) {
                return;
            }
            
            for (Paste paste : expiredPastes) {
                try {
                    analyticsRepository.deleteByShortlink(paste.getShortlink());
                    storageService.deletePaste(paste.getPastePath());
                    pasteRepository.delete(paste);
                } catch (Exception e) {
                    log.error("Failed to delete expired paste: {}", paste.getShortlink(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error during expired pastes cleanup job", e);
        }
    }
}
