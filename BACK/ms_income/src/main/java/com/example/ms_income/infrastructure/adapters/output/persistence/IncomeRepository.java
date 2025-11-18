package com.example.ms_income.infrastructure.adapters.output.persistence;

import com.example.ms_income.domain.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    Optional<Income> findByIncomeIdAndActiveTrue(Long incomeId);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.active = true")
    List<Income> findByUserIdAndActiveTrue(@Param("userId") Long userId);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.incomeCategoryId = :categoryId AND i.active = true")
    List<Income> findByUserIdAndIncomeCategoryIdAndActiveTrue(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.incomeDate BETWEEN :startDate AND :endDate AND i.active = true")
    List<Income> findByUserIdAndIncomeDateBetweenAndActiveTrue(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.amount BETWEEN :minAmount AND :maxAmount AND i.active = true")
    List<Income> findByUserIdAndAmountBetweenAndActiveTrue(
            @Param("userId") Long userId,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);
}
