#!/bin/bash
set -e

echo "🚀 Deploying FinPath Development Environment"
echo "============================================="

# Navigate to ops directory
cd "$(dirname "$0")/.."

# Pull latest changes from dev branch
echo "📥 Pulling latest changes from dev branch..."
git pull origin dev

# Pull latest Docker images
echo "🐳 Pulling latest Docker images..."
docker compose -f docker-compose.dev.yml pull

# Restart all services
echo "♻️  Restarting all services..."
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.dev.yml up -d

# Wait for services to start
echo "⏳ Waiting for services to start..."
sleep 10

# Show service status
echo ""
echo "📊 Service Status:"
docker compose -f docker-compose.dev.yml ps

# Test backend health
echo ""
echo "🔍 Testing backend health..."
docker logs finpath-backend-dev --tail 20

echo ""
echo "✅ Deployment complete!"
echo ""
echo "Test the endpoints:"
echo "  - Frontend: https://dev.css-appli24.com"
echo "  - API: https://dev.css-appli24.com/api/"
echo "  - Health: https://dev.css-appli24.com/actuator/health"
