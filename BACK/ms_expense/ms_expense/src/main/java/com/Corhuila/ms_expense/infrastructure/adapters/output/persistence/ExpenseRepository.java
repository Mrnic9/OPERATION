package com.Corhuila.ms_expense.infrastructure.adapters.output.persistence;

import com.Corhuila.ms_expense.domain.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByExpenseIdAndActiveTrue(Long expenseId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.active = true")
    List<Expense> findByUserIdAndActiveTrue(@Param("userId") Long userId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.expenseCategoryId = :categoryId AND e.active = true")
    List<Expense> findByUserIdAndExpenseCategoryIdAndActiveTrue(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :startDate AND :endDate AND e.active = true")
    List<Expense> findByUserIdAndExpenseDateBetweenAndActiveTrue(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.amount BETWEEN :minAmount AND :maxAmount AND e.active = true")
    List<Expense> findByUserIdAndAmountBetweenAndActiveTrue(
            @Param("userId") Long userId,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);
}