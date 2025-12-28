#!/bin/bash

echo "🔍 FinPath Dev Environment Debugging"
echo "======================================"
echo ""

cd /opt/finpath-dev

echo "1️⃣ Git Status"
echo "-------------"
git status
git log --oneline -3
echo ""

echo "2️⃣ Running Containers"
echo "--------------------"
docker ps --filter "name=finpath" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

echo "3️⃣ Backend Container Logs (last 20 lines)"
echo "-----------------------------------------"
docker logs finpath-backend-dev --tail 20
echo ""

echo "4️⃣ Nginx Proxy Logs (last 20 lines)"
echo "------------------------------------"
docker logs finpath-proxy-dev --tail 20
echo ""

echo "5️⃣ Nginx Configuration Being Used"
echo "----------------------------------"
docker exec finpath-proxy-dev cat /etc/nginx/conf.d/default.conf | head -30
echo ""

echo "6️⃣ Test Backend from Inside Proxy Container"
echo "--------------------------------------------"
docker exec finpath-proxy-dev wget -O- http://backend-dev:9080/api/db 2>&1 | grep -A 5 "{"
echo ""

echo "7️⃣ Test Backend Directly (bypass nginx)"
echo "----------------------------------------"
curl http://localhost:9080/api/db 2>/dev/null || echo "Backend not accessible on host"
echo ""

echo "8️⃣ Docker Network Info"
echo "----------------------"
docker network inspect finpath-net | grep -A 3 "finpath-backend-dev"
echo ""

echo "9️⃣ Check if Backend is Listening on Port 9080"
echo "----------------------------------------------"
docker exec finpath-backend-dev netstat -tlnp 2>/dev/null | grep 9080 || echo "netstat not available, trying wget..."
docker exec finpath-backend-dev wget -O- http://localhost:9080/actuator/health 2>&1 | tail -5
echo ""

echo "🔟 Environment Variables in Backend"
echo "-----------------------------------"
docker exec finpath-backend-dev env | grep -E "(SPRING_PROFILES_ACTIVE|DB_HOST|DB_PORT)"
echo ""
