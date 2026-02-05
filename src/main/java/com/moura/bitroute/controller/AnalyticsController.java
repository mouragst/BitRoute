package com.moura.bitroute.controller;

import com.moura.bitroute.dto.MonthlyViewData;
import com.moura.bitroute.dto.MonthlyViewsResponse;
import com.moura.bitroute.dto.TotalViewsResponse;
import com.moura.bitroute.dto.ViewsByMonthResponse;
import com.moura.bitroute.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;

    @GetMapping("/{shortlink}/total")
    public ResponseEntity<TotalViewsResponse> getTotalViews(@PathVariable String shortlink) {
        Long totalViews = analyticsService.getTotalViews(shortlink);
        
        TotalViewsResponse response = TotalViewsResponse.builder()
                .shortlink(shortlink)
                .totalViews(totalViews)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortlink}/monthly")
    public ResponseEntity<MonthlyViewsResponse> getMonthlyViews(@PathVariable String shortlink) {
        List<Map<String, Object>> monthlyViews = analyticsService.getMonthlyViews(shortlink);
        
        List<MonthlyViewData> monthlyViewDataList = monthlyViews.stream()
                        .map(view -> MonthlyViewData.builder()
                        .yearMonth((Integer) view.get("year_month"))
                        .views(((Number) view.get("views")).longValue())
                        .build())
                        .collect(Collectors.toList());
        
        MonthlyViewsResponse response = MonthlyViewsResponse.builder()
                .shortlink(shortlink)
                .monthlyViews(monthlyViewDataList)
                .build();
        
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/{shortlink}/month/{yearMonth}")
    public ResponseEntity<ViewsByMonthResponse> getViewsByMonth(
            @PathVariable String shortlink,
            @PathVariable Integer yearMonth) {
        
        Long views = analyticsService.getViewsByMonth(shortlink, yearMonth);

        ViewsByMonthResponse response = ViewsByMonthResponse.builder()
                .shortlink(shortlink)
                .yearMonth(yearMonth)
                .views(views)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
