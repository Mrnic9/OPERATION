package com.example.ms_income.domain.ports;

import com.example.ms_income.domain.model.Income;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncomeRepositoryPort {
    Income save(Income income);
    Optional<Income> findByIdAndActiveTrue(Long id);
    List<Income> findAllActive();
    List<Income> findByUserIdAndActiveTrue(Long userId);
    List<Income> findByUserIdAndIncomeCategoryIdAndActiveTrue(Long userId, Long categoryId);
    List<Income> findByUserIdAndIncomeDateBetweenAndActiveTrue(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Income> findByUserIdAndAmountBetweenAndActiveTrue(Long userId, BigDecimal minAmount, BigDecimal maxAmount);
}
