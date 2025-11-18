# Integración del ms_categories con otros Microservicios

## Arquitectura de Microservicios

Este sistema está compuesto por 3 microservicios independientes:

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   ms_income     │      │  ms_categories  │      │   ms_expense    │
│   Puerto: 8100  │      │   Puerto: 8090  │      │   Puerto: 8080  │
└─────────────────┘      └─────────────────┘      └─────────────────┘
        │                         │                         │
        └─────────────────────────┴─────────────────────────┘
                                  │
                    ┌─────────────────────────┐
                    │  PostgreSQL Database    │
                    │  gestion_gastos_dev     │
                    └─────────────────────────┘
```

## Modelo de Base de Datos Compartida

### Opción 1: Base de Datos Compartida (Configuración Actual)
Todos los microservicios se conectan a la misma base de datos PostgreSQL `gestion_gastos_dev`, pero cada uno gestiona sus propias tablas:

- **ms_categories** → Tabla `categories`
- **ms_income** → Tabla `incomes`
- **ms_expense** → Tabla `expenses`

#### Ventajas:
- Simplicidad en desarrollo
- Transacciones más sencillas
- No requiere llamadas HTTP entre servicios

#### Desventajas:
- Acoplamiento a nivel de base de datos
- Menos independencia entre servicios

### Opción 2: Bases de Datos Separadas (Recomendado para Producción)
Cada microservicio tiene su propia base de datos y se comunican vía API REST.

## Relaciones entre Microservicios

### 1. ms_income ↔ ms_categories

**Relación:**
- Un `Income` tiene una categoría de tipo `INCOME`
- La relación se establece mediante el campo `incomeCategoryId` en la tabla `incomes`

**Estructura:**
```sql
-- Tabla incomes (ms_income)
CREATE TABLE incomes (
    income_id BIGSERIAL PRIMARY KEY,
    income_category_id BIGINT NOT NULL,  -- Referencia a categories.category_id
    ...
);

-- Tabla categories (ms_categories)
CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,  -- 'INCOME' o 'EXPENSE'
    ...
);
```

**Validación:**
Antes de crear un ingreso, el ms_income puede:
1. Consultar `GET http://localhost:8090/api/v1/categories/{id}` para validar que la categoría existe
2. Consultar `GET http://localhost:8090/api/v1/categories/type/INCOME` para obtener categorías válidas de tipo INCOME

### 2. ms_expense ↔ ms_categories

**Relación:**
- Un `Expense` tiene una categoría de tipo `EXPENSE`
- La relación se establece mediante el campo `expenseCategoryId` en la tabla `expenses`

**Estructura:**
```sql
-- Tabla expenses (ms_expense)
CREATE TABLE expenses (
    expense_id BIGSERIAL PRIMARY KEY,
    expense_category_id BIGINT NOT NULL,  -- Referencia a categories.category_id
    ...
);
```

**Validación:**
Antes de crear un gasto, el ms_expense puede:
1. Consultar `GET http://localhost:8090/api/v1/categories/{id}` para validar que la categoría existe
2. Consultar `GET http://localhost:8090/api/v1/categories/type/EXPENSE` para obtener categorías válidas de tipo EXPENSE

## Configuración de Base de Datos

### Ambiente de Desarrollo (dev)

**ms_categories:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestion_gastos_dev
spring.datasource.username=postgres
spring.datasource.password=dev123456
```

**ms_income:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestion_gastos_dev
spring.datasource.username=postgres
spring.datasource.password=dev123456
```

**ms_expense:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_db_dev
spring.datasource.username=postgres
spring.datasource.password=dev123456
```

> **Nota:** Actualmente ms_expense usa una base de datos diferente. Para mayor consistencia, se puede cambiar a `gestion_gastos_dev`.

## Flujo de Trabajo Recomendado

### 1. Crear Categorías (Primero)
```bash
# Crear categoría de ingreso
curl -X POST http://localhost:8090/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Salario",
    "description": "Ingreso por salario mensual",
    "type": "INCOME"
  }'

# Respuesta: { "categoryId": 1, ... }
```

### 2. Crear Ingreso usando la categoría
```bash
# Crear ingreso con categoryId = 1
curl -X POST http://localhost:8100/api/v1/incomes \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 5000.00,
    "incomeCategoryId": 1,
    "incomeDate": "2025-10-21",
    "description": "Salario de Octubre"
  }'
```

### 3. Consultar Ingresos por Categoría
```bash
# Obtener todos los ingresos de una categoría específica
curl http://localhost:8100/api/v1/incomes/user/1/category/1
```

## Mejoras Sugeridas

### 1. Validación de Categorías
Implementar validación en ms_income y ms_expense antes de crear registros:

```java
// En IncomeUseCase (ms_income)
public IncomeResponse createIncome(IncomeRequest request) {
    // Validar que la categoría existe y es de tipo INCOME
    CategoryResponse category = categoryClient.getCategoryById(request.getIncomeCategoryId());

    if (category == null || category.getType() != CategoryType.INCOME) {
        throw new InvalidCategoryException("La categoría debe ser de tipo INCOME");
    }

    // Continuar con la creación del ingreso
    // ...
}
```

### 2. Cliente REST para comunicación
Implementar Feign Client o RestTemplate para comunicación entre servicios:

```java
@FeignClient(name = "ms-categories", url = "http://localhost:8090")
public interface CategoryClient {
    @GetMapping("/api/v1/categories/{id}")
    CategoryResponse getCategoryById(@PathVariable Long id);

    @GetMapping("/api/v1/categories/type/{type}")
    List<CategoryResponse> getCategoriesByType(@PathVariable String type);
}
```

### 3. Manejo de Eliminación
Cuando se elimina (soft delete) una categoría, considerar:
- ¿Qué pasa con los ingresos/gastos existentes con esa categoría?
- Opciones:
  - Mantener el ID pero mostrar "Categoría eliminada"
  - Migrar a una categoría por defecto
  - Prevenir eliminación si hay registros asociados

### 4. Cache
Implementar cache en ms_income y ms_expense para categorías:
```java
@Cacheable(value = "categories", key = "#id")
public CategoryResponse getCategoryById(Long id) {
    // Llamada al ms_categories
}
```

## Testing de Integración

### Script de prueba completa:
```bash
# 1. Levantar todos los microservicios
cd ms_categories && ./mvnw spring-boot:run &
cd ms_income && ./mvnw spring-boot:run &
cd ms_expense && ./mvnw spring-boot:run &

# 2. Esperar a que inicien
sleep 10

# 3. Crear categorías
curl -X POST http://localhost:8090/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Salario", "type": "INCOME"}'

curl -X POST http://localhost:8090/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Alimentación", "type": "EXPENSE"}'

# 4. Verificar categorías creadas
curl http://localhost:8090/api/v1/categories

# 5. Crear ingreso
curl -X POST http://localhost:8100/api/v1/incomes \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 5000,
    "incomeCategoryId": 1,
    "incomeDate": "2025-10-21"
  }'

# 6. Crear gasto
curl -X POST http://localhost:8080/api/v1/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 150,
    "expenseCategoryId": 2,
    "expenseDate": "2025-10-21"
  }'
```

## Monitoreo

Verificar el estado de todos los servicios:
```bash
# ms_categories
curl http://localhost:8090/actuator/health

# ms_income
curl http://localhost:8100/actuator/health

# ms_expense
curl http://localhost:8080/actuator/health
```
