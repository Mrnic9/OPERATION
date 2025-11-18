package com.example.ms_income.application.usecases;

import com.example.ms_income.domain.model.Income;
import com.example.ms_income.domain.model.dto.CategoryInfo;
import com.example.ms_income.domain.model.dto.IncomeResponse;
import com.example.ms_income.domain.model.dto.IncomeRequest;
import com.example.ms_income.domain.model.dto.IncomeUpdateRequest;
import com.example.ms_income.domain.ports.IncomeRepositoryPort;
import com.example.ms_income.domain.ports.IncomeServicePort;
import com.example.ms_income.infrastructure.adapters.output.external.CategoryClient;
import com.example.ms_income.infrastructure.adapters.output.external.CategoryResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeUseCase implements IncomeServicePort {

    private static final Logger logger = LoggerFactory.getLogger(IncomeUseCase.class);
    private final IncomeRepositoryPort incomeRepositoryPort;
    private final CategoryClient categoryClient;

    @Autowired
    public IncomeUseCase(IncomeRepositoryPort incomeRepositoryPort, CategoryClient categoryClient) {
        this.incomeRepositoryPort = incomeRepositoryPort;
        this.categoryClient = categoryClient;
    }

    @Override
    public IncomeResponse createIncome(IncomeRequest request) {
        logger.info("Iniciando creación de ingreso para usuario: {} con monto: {}", request.getUserId(), request.getAmount());


        if (!categoryClient.validateExpenseCategoryExists(request.getIncomeCategoryId())){
            logger.info("Iniciando creacion de Ingreso para usuario: {} con monto: {}", request.getUserId(), request.getAmount());

            throw new RuntimeException("la categoria con ID " + request.getIncomeCategoryId() + " no existe, no esta activa o no es de tipo INCOME");
        }

        Income income = Income.builder()
                .incomeDate(request.getIncomeDate().atStartOfDay())
                .amount(request.getAmount())
                .incomeCategoryId(request.getIncomeCategoryId())
                .description(request.getDescription())
                .userId(request.getUserId())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Income savedIncome = incomeRepositoryPort.save(income);
        logger.info("Ingreso creado exitosamente con ID: {} para usuario: {}", savedIncome.getIncomeId(), savedIncome.getUserId());

        return mapToResponse(savedIncome);
    }

    @Override
    public IncomeResponse getIncomeById(Long id) {
        logger.info("Obteniendo ingreso con ID: {}", id);

        Income income = incomeRepositoryPort.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró ingreso activo con ID: {}", id);
                    return new RuntimeException("Ingreso no encontrado con ID: " + id);
                });

        logger.info("Ingreso encontrado con ID: {} para usuario: {}", id, income.getUserId());
        return mapToResponse(income);
    }

    @Override
    public List<IncomeResponse> getAllIncomes() {
        logger.info("Obteniendo todos los ingresos activos");

        List<IncomeResponse> incomes = incomeRepositoryPort.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} ingresos activos", incomes.size());
        return incomes;
    }

    @Override
    public List<IncomeResponse> getIncomesByUserId(Long userId) {
        logger.info("Obteniendo todos los ingresos del usuario: {}", userId);

        List<IncomeResponse> incomes = incomeRepositoryPort.findByUserIdAndActiveTrue(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} ingresos para el usuario: {}", incomes.size(), userId);
        return incomes;
    }

    @Override
    public List<IncomeResponse> getIncomesByUserIdAndCategory(Long userId, Long categoryId) {
        logger.info("Filtrando ingresos por usuario: {} y categoría: {}", userId, categoryId);

        List<IncomeResponse> incomes = incomeRepositoryPort.findByUserIdAndIncomeCategoryIdAndActiveTrue(userId, categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} ingresos para usuario: {} y categoría: {}", incomes.size(), userId, categoryId);
        return incomes;
    }

    @Override
    public List<IncomeResponse> getIncomesByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Filtrando ingresos por usuario: {} en el rango de fechas: {} a {}", userId, startDate.toLocalDate(), endDate.toLocalDate());

        List<IncomeResponse> incomes = incomeRepositoryPort.findByUserIdAndIncomeDateBetweenAndActiveTrue(userId, startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} ingresos para usuario: {} en el rango de fechas especificado", incomes.size(), userId);
        return incomes;
    }

    @Override
    public List<IncomeResponse> getIncomesByUserIdAndAmountRange(Long userId, BigDecimal minAmount, BigDecimal maxAmount) {
        logger.info("Filtrando ingresos por usuario: {} en el rango de montos: {} a {}", userId, minAmount, maxAmount);

        List<IncomeResponse> incomes = incomeRepositoryPort.findByUserIdAndAmountBetweenAndActiveTrue(userId, minAmount, maxAmount).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} ingresos para usuario: {} en el rango de montos especificado", incomes.size(), userId);
        return incomes;
    }

    @Override
    public IncomeResponse updateIncome(Long id, IncomeUpdateRequest request) {
        logger.info("Iniciando actualización de ingreso con ID: {}", id);

        Income income = incomeRepositoryPort.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró ingreso activo con ID: {}", id);
                    return new RuntimeException("Ingreso no encontrado con ID: " + id);
                });

        if (request.getIncomeDate() != null) {
            logger.info("Actualizando fecha de {} a {}", income.getIncomeDate().toLocalDate(), request.getIncomeDate());
            income.setIncomeDate(request.getIncomeDate().atStartOfDay());
        }
        if (request.getAmount() != null) {
            logger.info("Actualizando monto de {} a {}", income.getAmount(), request.getAmount());
            income.setAmount(request.getAmount());
        }
        if (request.getIncomeCategoryId() != null) {
            if (!categoryClient.validateExpenseCategoryExists(request.getIncomeCategoryId())) {
                logger.error("Categoría inválida con ID: {}", request.getIncomeCategoryId());
                throw new RuntimeException("La categoría con ID " + request.getIncomeCategoryId() +
                        " no existe, no está activa o no es de tipo EXPENSE");
            }
            logger.info("Actualizando categoría de {} a {}", income.getIncomeCategoryId(), request.getIncomeCategoryId());
            income.setIncomeCategoryId(request.getIncomeCategoryId());
        }
        if (request.getDescription() != null) {
            logger.info("Actualizando descripción del ingreso");
            income.setDescription(request.getDescription());
        }
        income.setUpdatedAt(LocalDateTime.now());

        Income updatedIncome = incomeRepositoryPort.save(income);
        logger.info("Ingreso actualizado exitosamente con ID: {}", updatedIncome.getIncomeId());

        return mapToResponse(updatedIncome);
    }

    @Override
    public void deleteIncome(Long id) {
        logger.info("Iniciando eliminación lógica de ingreso con ID: {}", id);

        Income income = incomeRepositoryPort.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró ingreso activo con ID: {} para eliminar", id);
                    return new RuntimeException("Ingreso no encontrado con ID: " + id);
                });

        income.setActive(false);
        income.setUpdatedAt(LocalDateTime.now());
        incomeRepositoryPort.save(income);

        logger.info("Ingreso eliminado lógicamente exitosamente con ID: {}", id);
    }

    private IncomeResponse mapToResponse(Income income) {
        // Obtener información de la categoría
        CategoryInfo categoryInfo = null;
        try {
            CategoryResponse categoryResponse = categoryClient.getCategoryById(income.getIncomeCategoryId());
            if (categoryResponse != null) {
                categoryInfo = CategoryInfo.builder()
                        .name(categoryResponse.getName())
                        .description(categoryResponse.getDescription())
                        .type(categoryResponse.getType() != null ? categoryResponse.getType().toString() : null)
                        .build();
                logger.debug("Información de categoría obtenida para el gasto ID: {}", income.getIncomeId());
            } else {
                logger.warn("No se pudo obtener información de la categoría ID: {} para el gasto ID: {}",
                        income.getIncomeCategoryId(), income.getIncomeId());
            }
        } catch (Exception e) {
            logger.error("Error al obtener información de la categoría ID: {} para el gasto ID: {}",
                    income.getIncomeCategoryId(), income.getIncomeCategoryId(), e);
        }


        return IncomeResponse.builder()
                .incomeId(income.getIncomeId())
                .incomeDate(income.getIncomeDate())
                .amount(income.getAmount())
                .category(categoryInfo)
                .description(income.getDescription())
                .userId(income.getUserId())
                .active(income.getActive())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
