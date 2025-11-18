# Ejemplos de Integración con ms-categories

## Requisitos previos
- El microservicio ms-categories debe estar corriendo en `http://localhost:8090`
- Debe existir al menos una categoría de tipo EXPENSE en el sistema

## 1. Crear una categoría de tipo EXPENSE

```bash
curl --location 'http://localhost:8090/api/v1/categories' \
--header 'Content-Type: application/json' \
--data '{
  "name": "Alimentación",
  "description": "Gastos en comida y bebidas",
  "type": "EXPENSE"
}'
```

Respuesta esperada:
```json
{
  "categoryId": 1,
  "name": "Alimentación",
  "description": "Gastos en comida y bebidas",
  "type": "EXPENSE",
  "active": true,
  "createdAt": "2025-10-24T14:25:50.148166",
  "updatedAt": "2025-10-24T14:25:50.148171"
}
```

## 2. Crear un gasto asociado a la categoría

```bash
curl --location 'http://localhost:8080/api/v1/expenses' \
--header 'Content-Type: application/json' \
--data '{
  "amount": 150.50,
  "expenseCategoryId": 1,
  "expenseDate": "2025-10-24",
  "description": "Compra en supermercado",
  "userId": 1
}'
```

Respuesta esperada:
```json
{
  "expenseId": 1,
  "amount": 150.50,
  "expenseCategoryId": 1,
  "category": {
    "name": "Alimentación",
    "description": "Gastos en comida y bebidas",
    "type": "EXPENSE"
  },
  "expenseDate": "2025-10-24T00:00:00",
  "description": "Compra en supermercado",
  "userId": 1,
  "active": true,
  "createdAt": "2025-10-24T14:30:00.000000",
  "updatedAt": "2025-10-24T14:30:00.000000"
}
```

## 3. Obtener todos los gastos (con información de categoría)

```bash
curl --location 'http://localhost:8080/api/v1/expenses'
```

Respuesta esperada:
```json
[
  {
    "expenseId": 1,
    "amount": 150.50,
    "expenseCategoryId": 1,
    "category": {
      "name": "Alimentación",
      "description": "Gastos en comida y bebidas",
      "type": "EXPENSE"
    },
    "expenseDate": "2025-10-24T00:00:00",
    "description": "Compra en supermercado",
    "userId": 1,
    "active": true,
    "createdAt": "2025-10-24T14:30:00.000000",
    "updatedAt": "2025-10-24T14:30:00.000000"
  }
]
```

## 4. Actualizar un gasto cambiando la categoría

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/expenses/1' \
--header 'Content-Type: application/json' \
--data '{
  "expenseCategoryId": 2,
  "amount": 175.00
}'
```

## Validaciones Implementadas

### Al crear un gasto:
1. Se valida que la categoría exista
2. Se valida que la categoría esté activa
3. Se valida que la categoría sea de tipo EXPENSE
4. Si alguna validación falla, se retorna un error 500 con el mensaje descriptivo

### Al actualizar un gasto:
1. Si se cambia la categoría, se aplican las mismas validaciones que al crear

### Al consultar gastos:
1. Se enriquece la respuesta con la información de la categoría (name, description, type)
2. Si el servicio de categorías no está disponible, el campo "category" será null pero el gasto se retorna igual

## Casos de Error

### Intentar crear un gasto con categoría de tipo INCOME
```bash
curl --location 'http://localhost:8080/api/v1/expenses' \
--header 'Content-Type: application/json' \
--data '{
  "amount": 150.50,
  "expenseCategoryId": 5,
  "expenseDate": "2025-10-24",
  "description": "Esto fallará",
  "userId": 1
}'
```

Respuesta:
```json
{
  "error": "La categoría con ID 5 no existe, no está activa o no es de tipo EXPENSE"
}
```

### Intentar crear un gasto con categoría inexistente
```bash
curl --location 'http://localhost:8080/api/v1/expenses' \
--header 'Content-Type: application/json' \
--data '{
  "amount": 150.50,
  "expenseCategoryId": 999,
  "expenseDate": "2025-10-24",
  "description": "Esto fallará",
  "userId": 1
}'
```

Respuesta:
```json
{
  "error": "La categoría con ID 999 no existe, no está activa o no es de tipo EXPENSE"
}
```

## Configuración

La URL del microservicio de categorías se configura en `application-dev.properties`:

```properties
categories.service.url=${CATEGORIES_SERVICE_URL:http://localhost:8090}
```

Para cambiar la URL, puedes:
1. Modificar el archivo de propiedades
2. Usar variable de entorno: `CATEGORIES_SERVICE_URL=http://otro-host:8090`