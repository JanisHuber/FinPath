#!/bin/bash
set -e

echo "🚀 Deploying FinPath Production Environment"
echo "============================================"

# Navigate to ops directory
cd "$(dirname "$0")/.."

# Pull latest changes from main branch
echo "📥 Pulling latest changes from main branch..."
git pull origin main

# Pull latest Docker images
echo "🐳 Pulling latest Docker images..."
docker compose -f docker-compose.prod.yml pull

# Restart all services with zero-downtime
echo "♻️  Performing rolling update..."
docker compose -f docker-compose.prod.yml up -d --remove-orphans

# Wait for services to start
echo "⏳ Waiting for services to start..."
sleep 15

# Show service status
echo ""
echo "📊 Service Status:"
docker compose -f docker-compose.prod.yml ps

# Test backend health
echo ""
echo "🔍 Testing backend health..."
docker logs finpath-backend-prod --tail 20

# Show nginx logs
echo ""
echo "🔍 Nginx Proxy logs..."
docker logs finpath-proxy --tail 10

echo ""
echo "✅ Deployment complete!"
echo ""
echo "Test the endpoints:"
echo "  - Frontend: https://css-appli24.com"
echo "  - API: https://css-appli24.com/api/"
echo "  - Health: https://css-appli24.com/actuator/health"
