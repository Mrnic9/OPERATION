package com.example.ms_income.domain.ports;

import com.example.ms_income.domain.model.dto.IncomeRequest;
import com.example.ms_income.domain.model.dto.IncomeResponse;
import com.example.ms_income.domain.model.dto.IncomeUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IncomeServicePort {
    IncomeResponse createIncome(IncomeRequest request);
    IncomeResponse getIncomeById(Long id);
    List<IncomeResponse> getAllIncomes();
    List<IncomeResponse> getIncomesByUserId(Long userId);
    List<IncomeResponse> getIncomesByUserIdAndCategory(Long userId, Long categoryId);
    List<IncomeResponse> getIncomesByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<IncomeResponse> getIncomesByUserIdAndAmountRange(Long userId, BigDecimal minAmount, BigDecimal maxAmount);
    IncomeResponse updateIncome(Long id, IncomeUpdateRequest request);
    void deleteIncome(Long id);
}
