package com.moura.bitroute.repository;

import com.moura.bitroute.model.PasteAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PasteAnalyticsRepository extends JpaRepository<PasteAnalytics, Long> {
    
    Optional<PasteAnalytics> findByShortlinkAndYearMonth(String shortlink, Integer yearMonth);
    
    @Modifying
    @Query("UPDATE PasteAnalytics pa SET pa.viewCount = pa.viewCount + 1 WHERE pa.shortlink = :shortlink AND pa.yearMonth = :yearMonth")
    int incrementViewCount(@Param("shortlink") String shortlink, @Param("yearMonth") Integer yearMonth);
    
    @Query("SELECT SUM(pa.viewCount) FROM PasteAnalytics pa WHERE pa.shortlink = :shortlink")
    Long sumViewsByShortlink(@Param("shortlink") String shortlink);
    
    @Query("SELECT pa.viewCount FROM PasteAnalytics pa WHERE pa.shortlink = :shortlink AND pa.yearMonth = :yearMonth")
    Long getViewsByShortlinkAndMonth(@Param("shortlink") String shortlink, @Param("yearMonth") Integer yearMonth);
    
    @Query("SELECT pa.yearMonth as month, pa.viewCount as views FROM PasteAnalytics pa WHERE pa.shortlink = :shortlink ORDER BY pa.yearMonth")
    List<Map<String, Object>> getMonthlyViewsByShortlink(@Param("shortlink") String shortlink);
    
    @Modifying
    @Query("DELETE FROM PasteAnalytics pa WHERE pa.shortlink = :shortlink")
    void deleteByShortlink(@Param("shortlink") String shortlink);
}
