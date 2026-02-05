package com.moura.bitroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewPasteResponse {
    private String pasteContent;
    private LocalDateTime createdAt;
    private Integer expirationLengthInMinutes;
}
