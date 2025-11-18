package com.example.ms_income.domain.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeRequest {
    @NotNull(message = "Income date is required")
    private LocalDate incomeDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Amount must have at most 17 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Income category ID is required")
    @Positive(message = "Income category ID must be positive")
    private Long incomeCategoryId;

    @Size(max = 500, message = "Description must be 500 characters max")
    private String description;

    // User ID is extracted from JWT token, not from request body
    private Long userId;
}
