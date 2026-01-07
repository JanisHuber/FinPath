#!/bin/bash

echo "🔍 FinPath Environment Debugging"
echo "=================================="
echo ""

# Navigate to ops directory
cd "$(dirname "$0")/.."

# Check which environment is running
echo "1️⃣ Detecting Running Environment"
echo "--------------------------------"
if docker ps | grep -q "finpath-backend-dev"; then
    ENV="dev"
    BACKEND="finpath-backend-dev"
    FRONTEND="finpath-frontend-web-dev"
    COMPOSE_FILE="docker-compose.dev.yml"
    echo "🟢 Development environment detected"
elif docker ps | grep -q "finpath-backend-prod"; then
    ENV="prod"
    BACKEND="finpath-backend-prod"
    FRONTEND="finpath-frontend-web-prod"
    COMPOSE_FILE="docker-compose.prod.yml"
    echo "🟢 Production environment detected"
else
    echo "🔴 No FinPath environment running"
    exit 1
fi
echo ""

echo "2️⃣ Git Status"
echo "-------------"
git status --short
git log --oneline -3
echo ""

echo "3️⃣ Running Containers"
echo "--------------------"
docker ps --filter "name=finpath" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

echo "4️⃣ Backend Container Logs (last 30 lines)"
echo "-----------------------------------------"
docker logs $BACKEND --tail 30
echo ""

if [ "$ENV" = "prod" ]; then
    echo "5️⃣ Nginx Proxy Logs (last 20 lines)"
    echo "------------------------------------"
    docker logs finpath-proxy --tail 20
    echo ""

    echo "6️⃣ Nginx Configuration Check"
    echo "-----------------------------"
    docker exec finpath-proxy nginx -t
    echo ""

    echo "7️⃣ Test Backend from Inside Proxy Container"
    echo "--------------------------------------------"
    docker exec finpath-proxy wget -qO- http://backend-prod:9080/actuator/health || echo "Backend not reachable from proxy"
    echo ""
fi

echo "8️⃣ Test Backend Directly"
echo "------------------------"
if [ "$ENV" = "dev" ]; then
    curl -s http://localhost:9080/actuator/health 2>/dev/null || echo "Backend not accessible on host"
else
    docker exec finpath-proxy wget -qO- http://backend-prod:9080/actuator/health || echo "Backend not accessible"
fi
echo ""

echo "9️⃣ Docker Network Info"
echo "----------------------"
docker network inspect finpath-net | grep -A 3 "$BACKEND"
echo ""

echo "🔟 Environment Variables in Backend"
echo "-----------------------------------"
docker exec $BACKEND env | grep -E "(SPRING_PROFILES_ACTIVE|DB_HOST|DB_PORT|DB_NAME)"
echo ""

echo "📝 Summary"
echo "----------"
echo "Environment: $ENV"
echo "Compose file: $COMPOSE_FILE"
echo "Backend: $BACKEND"
echo "Frontend: $FRONTEND"
