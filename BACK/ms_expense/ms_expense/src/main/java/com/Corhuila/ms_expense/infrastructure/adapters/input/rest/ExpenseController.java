package com.Corhuila.ms_expense.infrastructure.adapters.input.rest;

import com.Corhuila.ms_expense.domain.model.dto.ExpenseRequest;
import com.Corhuila.ms_expense.domain.model.dto.ExpenseResponse;
import com.Corhuila.ms_expense.domain.model.dto.ExpenseUpdateRequest;
import com.Corhuila.ms_expense.domain.ports.ExpenseServicePort;
import com.Corhuila.ms_expense.infrastructure.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    private final ExpenseServicePort expenseServicePort;
    private final JwtUtil jwtUtil;

    @Autowired
    public ExpenseController(ExpenseServicePort expenseServicePort, JwtUtil jwtUtil) {
        this.expenseServicePort = expenseServicePort;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Extract userId from JWT token
     */
    private Long extractUserId(String authHeader) {
        String token = extractToken(authHeader);
        if (token != null) {
            return jwtUtil.extractUserId(token);
        }
        throw new IllegalArgumentException("Invalid or missing Authorization header");
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ExpenseRequest request) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);
        request.setUserId(userId);

        logger.info("Solicitud recibida para crear gasto - Usuario: {}, Monto: {}, Categoría: {}",
                userId, request.getAmount(), request.getExpenseCategoryId());

        ExpenseResponse response = expenseServicePort.createExpense(request);

        logger.info("Gasto creado exitosamente con ID: {}", response.getExpenseId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseUpdateRequest request) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para actualizar gasto con ID: {} del usuario: {}", id, userId);

        // Validate that the expense belongs to the authenticated user
        ExpenseResponse existingExpense = expenseServicePort.getExpenseById(id);
        if (!existingExpense.getUserId().equals(userId)) {
            logger.warn("Usuario {} intentó actualizar gasto {} que no le pertenece", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ExpenseResponse response = expenseServicePort.updateExpense(id, request);

        logger.info("Respuesta enviada para gasto actualizado con ID: {}", response.getExpenseId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para eliminar gasto con ID: {} del usuario: {}", id, userId);

        // Validate that the expense belongs to the authenticated user
        ExpenseResponse existingExpense = expenseServicePort.getExpenseById(id);
        if (!existingExpense.getUserId().equals(userId)) {
            logger.warn("Usuario {} intentó eliminar gasto {} que no le pertenece", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        expenseServicePort.deleteExpense(id);

        logger.info("Respuesta enviada - Gasto eliminado lógicamente con ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(
            @RequestHeader("Authorization") String authHeader) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para obtener todos los gastos del usuario: {}", userId);

        List<ExpenseResponse> responses = expenseServicePort.getExpensesByUserId(userId);

        logger.info("Respuesta enviada con {} gastos para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para filtrar gastos por fecha - Usuario: {}, Rango: {} a {}",
                userId, startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<ExpenseResponse> responses = expenseServicePort.getExpensesByUserIdAndDateRange(userId, startDateTime, endDateTime);

        logger.info("Respuesta enviada con {} gastos filtrados por fecha para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long categoryId) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para filtrar gastos por categoría - Usuario: {}, Categoría: {}",
                userId, categoryId);

        List<ExpenseResponse> responses = expenseServicePort.getExpensesByUserIdAndCategory(userId, categoryId);

        logger.info("Respuesta enviada con {} gastos filtrados por categoría para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/amount-range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByAmountRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para filtrar gastos por monto - Usuario: {}, Rango: {} a {}",
                userId, minAmount, maxAmount);

        List<ExpenseResponse> responses = expenseServicePort.getExpensesByUserIdAndAmountRange(userId, minAmount, maxAmount);

        logger.info("Respuesta enviada con {} gastos filtrados por monto para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }
}