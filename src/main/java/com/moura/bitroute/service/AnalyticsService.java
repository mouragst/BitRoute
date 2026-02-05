package com.moura.bitroute.service;

import com.moura.bitroute.dto.PasteViewEvent;
import com.moura.bitroute.exception.PasteNotFoundException;
import com.moura.bitroute.model.PasteAnalytics;
import com.moura.bitroute.repository.PasteAnalyticsRepository;
import com.moura.bitroute.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final PasteAnalyticsRepository analyticsRepository;
    private final PasteRepository pasteRepository;
 
    @Transactional
    public void recordView(PasteViewEvent event) {
        try {
            Integer yearMonth = calculateYearMonth(event.getViewedAt());
            
            int updated = analyticsRepository.incrementViewCount(event.getShortlink(), yearMonth);
            
            if (updated == 0) {
                PasteAnalytics analytics = PasteAnalytics.builder()
                        .shortlink(event.getShortlink())
                        .yearMonth(yearMonth)
                        .viewCount(1L)
                        .build();
                
                analyticsRepository.save(analytics);
            }
        } catch (Exception e) {
            log.error("Failed to record view for paste: {}", event.getShortlink(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public Long getTotalViews(String shortlink) {
        validatePasteExists(shortlink);
        Long total = analyticsRepository.sumViewsByShortlink(shortlink);
        return total != null ? total : 0L;
    }
    
    @Transactional(readOnly = true)
    public Long getViewsByMonth(String shortlink, Integer yearMonth) {
        validatePasteExists(shortlink);
        Long views = analyticsRepository.getViewsByShortlinkAndMonth(shortlink, yearMonth);
        return views != null ? views : 0L;
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyViews(String shortlink) {
        validatePasteExists(shortlink);
        return analyticsRepository.getMonthlyViewsByShortlink(shortlink);
    }
    
    private void validatePasteExists(String shortlink) {
        if (!pasteRepository.existsById(shortlink)) {
            throw new PasteNotFoundException(shortlink);
        }
    }
    
    private Integer calculateYearMonth(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonth().getValue();
        return year * 100 + month;
    }
}
