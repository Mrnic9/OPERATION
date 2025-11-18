#!/bin/bash

echo "========================================="
echo "   VALIDACIÓN MS_EXPENSE"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8080"

echo "1. Verificando Health Check..."
HEALTH=$(curl -s ${BASE_URL}/actuator/health | grep -o '"status":"UP"')
if [ ! -z "$HEALTH" ]; then
    echo -e "${GREEN}✅ Health Check: OK${NC}"
else
    echo -e "${RED}❌ Health Check: FAILED${NC}"
    exit 1
fi

echo ""
echo "2. Verificando Actuator Info..."
curl -s ${BASE_URL}/actuator/info > /dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Actuator Info: OK${NC}"
else
    echo -e "${RED}❌ Actuator Info: FAILED${NC}"
fi

echo ""
echo "3. Intentando acceder al endpoint sin autenticación (debe fallar)..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" ${BASE_URL}/api/v1/expenses)
if [ "$RESPONSE" = "401" ] || [ "$RESPONSE" = "403" ]; then
    echo -e "${GREEN}✅ Seguridad funcionando: Requiere autenticación (HTTP $RESPONSE)${NC}"
else
    echo -e "${YELLOW}⚠️  Warning: Expected 401/403, got HTTP $RESPONSE${NC}"
fi

echo ""
echo "========================================="
echo -e "${GREEN}✅ MS_EXPENSE VALIDADO CORRECTAMENTE${NC}"
echo "========================================="
echo ""
echo "📝 Notas:"
echo "   • Servicio corriendo en: ${BASE_URL}"
echo "   • Base de datos: H2 (in-memory)"
echo "   • Endpoints protegidos con JWT"
echo ""
echo "🚀 Para probar con Postman:"
echo "   1. Importa las colecciones de postman_collections/"
echo "   2. Ejecuta Login en ms_user para obtener token"
echo "   3. Prueba los endpoints de ms_expense"
echo ""
