package com.moura.bitroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewsByMonthResponse {
    private String shortlink;
    private Integer yearMonth;
    private Long views;
}
