package com.moura.bitroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasteViewEvent implements Serializable {
    private String shortlink;
    private LocalDateTime viewedAt;
}
