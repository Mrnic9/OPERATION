package com.example.ms_income.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponse {
    private Long incomeId;
    private LocalDateTime incomeDate;
    private BigDecimal amount;
    private CategoryInfo category;
    private String description;
    private Long userId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
