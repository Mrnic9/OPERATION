package com.Corhuila.ms_expense.domain.ports;

import com.Corhuila.ms_expense.domain.model.dto.ExpenseRequest;
import com.Corhuila.ms_expense.domain.model.dto.ExpenseResponse;
import com.Corhuila.ms_expense.domain.model.dto.ExpenseUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseServicePort {
    ExpenseResponse createExpense(ExpenseRequest request);
    ExpenseResponse getExpenseById(Long id);
    List<ExpenseResponse> getAllExpenses();
    List<ExpenseResponse> getExpensesByUserId(Long userId);
    List<ExpenseResponse> getExpensesByUserIdAndCategory(Long userId, Long categoryId);
    List<ExpenseResponse> getExpensesByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<ExpenseResponse> getExpensesByUserIdAndAmountRange(Long userId, BigDecimal minAmount, BigDecimal maxAmount);
    ExpenseResponse updateExpense(Long id, ExpenseUpdateRequest request);
    void deleteExpense(Long id);
}