package com.Corhuila.ms_expense.domain.ports;

import com.Corhuila.ms_expense.domain.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepositoryPort {
    Expense save(Expense expense);
    Optional<Expense> findByIdAndActiveTrue(Long id);
    List<Expense> findAllActive();
    List<Expense> findByUserIdAndActiveTrue(Long userId);
    List<Expense> findByUserIdAndExpenseCategoryIdAndActiveTrue(Long userId, Long categoryId);
    List<Expense> findByUserIdAndExpenseDateBetweenAndActiveTrue(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Expense> findByUserIdAndAmountBetweenAndActiveTrue(Long userId, BigDecimal minAmount, BigDecimal maxAmount);
}