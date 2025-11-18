package com.example.ms_income.infrastructure.adapters.input.rest;

import com.example.ms_income.domain.model.dto.IncomeRequest;
import com.example.ms_income.domain.model.dto.IncomeResponse;
import com.example.ms_income.domain.model.dto.IncomeUpdateRequest;
import com.example.ms_income.domain.ports.IncomeServicePort;
import com.example.ms_income.infrastructure.security.JwtUtil;
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
@RequestMapping("/api/v1/incomes")
@CrossOrigin(origins = "*")
public class IncomeController {

    private static final Logger logger = LoggerFactory.getLogger(IncomeController.class);
    private final IncomeServicePort incomeServicePort;
    private final JwtUtil jwtUtil;

    @Autowired
    public IncomeController(IncomeServicePort incomeServicePort, JwtUtil jwtUtil) {
        this.incomeServicePort = incomeServicePort;
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
    public ResponseEntity<IncomeResponse> createIncome(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody IncomeRequest request) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);
        request.setUserId(userId);

        logger.info("Solicitud recibida para crear ingreso - Usuario: {}, Monto: {}, Categoría: {}",
                userId, request.getAmount(), request.getIncomeCategoryId());

        IncomeResponse response = incomeServicePort.createIncome(request);

        logger.info("Ingreso creado exitosamente con ID: {}", response.getIncomeId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody IncomeUpdateRequest request) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para actualizar ingreso con ID: {} del usuario: {}", id, userId);

        // Validate that the income belongs to the authenticated user
        IncomeResponse existingIncome = incomeServicePort.getIncomeById(id);
        if (!existingIncome.getUserId().equals(userId)) {
            logger.warn("Usuario {} intentó actualizar ingreso {} que no le pertenece", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        IncomeResponse response = incomeServicePort.updateIncome(id, request);

        logger.info("Respuesta enviada para ingreso actualizado con ID: {}", response.getIncomeId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para eliminar ingreso con ID: {} del usuario: {}", id, userId);

        // Validate that the income belongs to the authenticated user
        IncomeResponse existingIncome = incomeServicePort.getIncomeById(id);
        if (!existingIncome.getUserId().equals(userId)) {
            logger.warn("Usuario {} intentó eliminar ingreso {} que no le pertenece", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        incomeServicePort.deleteIncome(id);

        logger.info("Respuesta enviada - Ingreso eliminado lógicamente con ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAllIncomes(
            @RequestHeader("Authorization") String authHeader) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para obtener todos los ingresos del usuario: {}", userId);

        List<IncomeResponse> responses = incomeServicePort.getIncomesByUserId(userId);

        logger.info("Respuesta enviada con {} ingresos para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<IncomeResponse>> getIncomesByDateRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para filtrar ingresos por fecha - Usuario: {}, Rango: {} a {}",
                userId, startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<IncomeResponse> responses = incomeServicePort.getIncomesByUserIdAndDateRange(userId, startDateTime, endDateTime);

        logger.info("Respuesta enviada con {} ingresos filtrados por fecha para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<IncomeResponse>> getIncomesByCategory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long categoryId) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para filtrar ingresos por categoría - Usuario: {}, Categoría: {}",
                userId, categoryId);

        List<IncomeResponse> responses = incomeServicePort.getIncomesByUserIdAndCategory(userId, categoryId);

        logger.info("Respuesta enviada con {} ingresos filtrados por categoría para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/amount-range")
    public ResponseEntity<List<IncomeResponse>> getIncomesByAmountRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {

        // Extract userId from JWT token
        Long userId = extractUserId(authHeader);

        logger.info("Solicitud recibida para filtrar ingresos por monto - Usuario: {}, Rango: {} a {}",
                userId, minAmount, maxAmount);

        List<IncomeResponse> responses = incomeServicePort.getIncomesByUserIdAndAmountRange(userId, minAmount, maxAmount);

        logger.info("Respuesta enviada con {} ingresos filtrados por monto para usuario: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }
}
