package com.moura.bitroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paste_analytics", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"shortlink", "`year_month`"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasteAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 7)
    private String shortlink;
    
    @Column(name = "`year_month`", nullable = false)
    private Integer yearMonth;
    
    @Column(name = "view_count", nullable = false)
    private Long viewCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shortlink", referencedColumnName = "shortlink", insertable = false, updatable = false)
    private Paste paste;
}
