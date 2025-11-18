package com.Corhuila.ms_expense.infrastructure.adapters.output.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long categoryId;
    private String name;
    private String description;
    private CategoryType type;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CategoryType {
        INCOME,
        EXPENSE
    }
}
