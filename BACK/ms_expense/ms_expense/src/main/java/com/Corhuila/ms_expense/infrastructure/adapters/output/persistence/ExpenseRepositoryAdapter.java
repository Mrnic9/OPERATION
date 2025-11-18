package com.Corhuila.ms_expense.infrastructure.adapters.output.persistence;

import com.Corhuila.ms_expense.domain.model.Expense;
import com.Corhuila.ms_expense.domain.ports.ExpenseRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ExpenseRepositoryAdapter implements ExpenseRepositoryPort {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseRepositoryAdapter(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> findByIdAndActiveTrue(Long id) {
        return expenseRepository.findByExpenseIdAndActiveTrue(id);
    }

    @Override
    public List<Expense> findAllActive() {
        return expenseRepository.findAll().stream()
                .filter(Expense::getActive)
                .toList();
    }

    @Override
    public List<Expense> findByUserIdAndActiveTrue(Long userId) {
        return expenseRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public List<Expense> findByUserIdAndExpenseCategoryIdAndActiveTrue(Long userId, Long categoryId) {
        return expenseRepository.findByUserIdAndExpenseCategoryIdAndActiveTrue(userId, categoryId);
    }

    @Override
    public List<Expense> findByUserIdAndExpenseDateBetweenAndActiveTrue(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return expenseRepository.findByUserIdAndExpenseDateBetweenAndActiveTrue(userId, startDate, endDate);
    }

    @Override
    public List<Expense> findByUserIdAndAmountBetweenAndActiveTrue(Long userId, BigDecimal minAmount, BigDecimal maxAmount) {
        return expenseRepository.findByUserIdAndAmountBetweenAndActiveTrue(userId, minAmount, maxAmount);
    }
}