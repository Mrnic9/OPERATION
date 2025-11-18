# Microservicio de Categorías (ms_categories)

## Descripción
Microservicio para la gestión de categorías de ingresos y gastos en el sistema de gestión financiera.

## Arquitectura
- **Patrón**: Arquitectura Hexagonal (Ports & Adapters)
- **Framework**: Spring Boot 3.5.6
- **Java**: 17
- **Base de datos**: PostgreSQL (producción) / H2 (desarrollo local)

## Estructura del Proyecto

```
ms_categories/
├── domain/
│   ├── model/
│   │   ├── Category.java              # Entidad principal
│   │   └── dto/                        # Data Transfer Objects
│   └── ports/                          # Interfaces (contratos)
├── application/
│   └── usecases/                       # Lógica de negocio
├── infrastructure/
│   ├── adapters/
│   │   ├── input/rest/                 # Controladores REST
│   │   └── output/persistence/         # Repositorios JPA
│   └── configuration/                  # Configuración de beans
```

## Modelo de Datos

### Entidad Category
- `categoryId` (Long): ID único
- `name` (String): Nombre de la categoría
- `description` (String): Descripción opcional
- `type` (CategoryType): INCOME o EXPENSE
- `active` (Boolean): Estado (soft delete)
- `createdAt` (LocalDateTime): Fecha de creación
- `updatedAt` (LocalDateTime): Fecha de actualización

## API Endpoints

Base URL: `http://localhost:8090/api/v1/categories`

### Crear categoría
```
POST /api/v1/categories
Content-Type: application/json

{
  "name": "Salario",
  "description": "Ingresos por salario mensual",
  "type": "INCOME"
}
```

### Actualizar categoría
```
PUT /api/v1/categories/{id}
Content-Type: application/json

{
  "name": "Salario Actualizado",
  "description": "Nueva descripción",
  "type": "INCOME"
}
```

### Eliminar categoría (soft delete)
```
DELETE /api/v1/categories/{id}
```

### Obtener todas las categorías
```
GET /api/v1/categories
```

### Obtener categoría por ID
```
GET /api/v1/categories/{id}
```

### Filtrar por tipo
```
GET /api/v1/categories/type/{type}
# type puede ser: INCOME o EXPENSE
```

## Configuración

### Perfiles disponibles
- `dev`: Desarrollo (PostgreSQL compartida con otros MS)
- `local`: Local (H2 en memoria)
- `qa`: QA/Testing
- `pdn`: Producción

### Variables de entorno
```bash
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gestion_gastos_dev
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=dev123456
```

## Ejecución

### Con Maven
```bash
cd ms_categories
mvn spring-boot:run
```

### Con perfil específico
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Puerto
El microservicio corre en el puerto `8090`

## Health Check
```
GET http://localhost:8090/actuator/health
```

## Integración con otros Microservicios

### Relación con ms_income
- Las categorías de tipo `INCOME` son referenciadas por el campo `incomeCategoryId` en la tabla `incomes`
- La relación es mediante ID (sin FK física para mantener independencia entre microservicios)

### Relación con ms_expense
- Las categorías de tipo `EXPENSE` son referenciadas por el campo `expenseCategoryId` en la tabla `expenses`
- La relación es mediante ID (sin FK física para mantener independencia entre microservicios)

## Base de Datos

### Configuración para desarrollo
Por defecto, en el perfil `dev`, el microservicio usa la misma base de datos PostgreSQL que los otros microservicios:
- Base de datos: `gestion_gastos_dev`
- Puerto: `5432`
- Usuario: `postgres`

### Tabla generada
La tabla `categories` se crea automáticamente con JPA en modo `update`.

## Logging
El microservicio implementa logging detallado en todas las operaciones:
- INFO: Operaciones exitosas y flujo normal
- ERROR: Errores y excepciones

## Características
- ✅ CRUD completo de categorías
- ✅ Soft delete (eliminación lógica)
- ✅ Filtrado por tipo (INCOME/EXPENSE)
- ✅ Validación de datos con Jakarta Validation
- ✅ Auditoría automática (createdAt, updatedAt)
- ✅ Logging detallado
- ✅ Health checks
- ✅ CORS habilitado
