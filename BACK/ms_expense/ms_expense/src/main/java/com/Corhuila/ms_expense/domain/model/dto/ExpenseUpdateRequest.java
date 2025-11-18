package com.Corhuila.ms_expense.domain.model.dto;

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
public class ExpenseUpdateRequest {
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Amount must have at most 17 integer digits and 2 decimal places")
    private BigDecimal amount;

    @Positive(message = "Expense category ID must be positive")
    private Long expenseCategoryId;

    private LocalDate expenseDate;

    @Size(max = 500, message = "Description must be 500 characters max")
    private String description;
}