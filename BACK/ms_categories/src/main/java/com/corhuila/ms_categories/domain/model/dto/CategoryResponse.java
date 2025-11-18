package com.corhuila.ms_categories.domain.model.dto;

import com.corhuila.ms_categories.domain.model.Category;
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
    private Category.CategoryType type;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
