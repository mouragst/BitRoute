package com.moura.bitroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyViewsResponse {
    private String shortlink;
    private List<MonthlyViewData> monthlyViews;
}
