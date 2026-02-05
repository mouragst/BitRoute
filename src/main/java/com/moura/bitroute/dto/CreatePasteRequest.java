package com.moura.bitroute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePasteRequest {
    private String pasteContent;
    private Integer expirationLengthInMinutes;
}
