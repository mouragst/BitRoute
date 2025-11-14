package com.moura.bitroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pastes")
public class Paste {
    
    @Id
    @Column(length = 7, nullable = false)
    private String shortlink;
    
    @Column(name = "expiration_length_in_minutes")
    private Integer expirationLengthInMinutes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "paste_path", length = 255, nullable = false)
    private String pastePath;
    
    public boolean isExpired() {
        if (expirationLengthInMinutes == null || createdAt == null) {
            return false;
        }
        LocalDateTime expirationTime = createdAt.plusMinutes(expirationLengthInMinutes);
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    public LocalDateTime getExpirationTime() {
        if (expirationLengthInMinutes == null || createdAt == null) {
            return null;
        }
        return createdAt.plusMinutes(expirationLengthInMinutes);
    }
}