# FinPath Operations Guide

Complete deployment and operations documentation for FinPath on Hetzner Cloud.

---

## 📁 Directory Structure

```
ops/
├── README.md                       # This file
├── CLOUDFLARE_SETUP.md             # Cloudflare configuration guide
├── docker-compose.dev.yml          # Development environment
├── docker-compose.prod.yml         # Production environment
├── nginx/
│   └── nginx.conf                  # All-in-One nginx config for both environments
├── scripts/
│   ├── deploy-dev.sh               # Deploy development environment
│   ├── deploy-prod.sh              # Deploy production environment
│   └── debug.sh                    # Debug script (auto-detects environment)
└── environments/
    ├── dev.env.template            # Development environment template
    ├── prod.env.template           # Production environment template
    ├── dev.env                     # Development secrets (gitignored)
    └── prod.env                    # Production secrets (gitignored)
```

---

## 🏗️ Architecture

Both environments run on a single Hetzner server using **Supabase** as the managed database.

```
Internet
   │
Cloudflare (SSL/TLS, DDoS Protection)
   │
   ├─→ dev.css-appli24.com  → Nginx → Frontend-Dev + Backend-Dev → Supabase (Session Pooler)
   │
   └─→ css-appli24.com      → Nginx → Frontend-Prod + Backend-Prod → Supabase (Session Pooler)
```

**Stack:**
- **Frontend**: Angular (Docker container, port 80)
- **Backend**: Spring Boot (Docker container, port 9080)
- **Reverse Proxy**: Nginx (Docker container, ports 80/443)
- **Database**: Supabase PostgreSQL (managed, external, with Session Pooler)
- **Network**: `finpath-net` (Docker network)

---

## 🚀 Quick Start - Hetzner Server Setup

### Prerequisites

1. **Hetzner Server** running Ubuntu 24.04
2. **DNS Records** pointing to your server:
   - `dev.css-appli24.com` → Server IP
   - `css-appli24.com` → Server IP
3. **Cloudflare** configured (see `CLOUDFLARE_SETUP.md`)
4. **Supabase** project created with Session Pooler enabled
5. **GitHub Container Registry** access configured

### Initial Server Setup

```bash
# 1. Update system and install Docker
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io docker-compose-plugin git

# 2. Start Docker
sudo systemctl enable docker
sudo systemctl start docker

# 3. Create deploy user
sudo useradd -m -s /bin/bash -G docker deploy
sudo usermod -aG sudo deploy

# 4. Set up SSH for deploy user
sudo mkdir -p /home/deploy/.ssh
sudo chmod 700 /home/deploy/.ssh
echo "YOUR_SSH_PUBLIC_KEY" | sudo tee /home/deploy/.ssh/authorized_keys
sudo chmod 600 /home/deploy/.ssh/authorized_keys
sudo chown -R deploy:deploy /home/deploy/.ssh

# 5. Switch to deploy user
sudo su - deploy
```

### Clone Repository & Configure

```bash
# Create project directory
mkdir -p /opt/finpath
cd /opt/finpath

# Clone repository
git clone https://github.com/JanisHuber/FinPath.git .

# Navigate to ops directory
cd ops

# Configure environment variables
cd environments

# Development environment
cp dev.env.template dev.env
nano dev.env  # Add your Supabase JDBC URL

# Production environment
cp prod.env.template prod.env
nano prod.env  # Add your Supabase JDBC URL

cd ..
```

### Supabase Configuration

#### 1. Get JDBC URL from Supabase

```bash
1. Go to Supabase Dashboard
2. Your Project → Settings → Database
3. Connection String section
4. Mode: Select "Session pooler" (NOT Transaction or Direct)
5. Copy the JDBC connection string
```

**Example JDBC URL:**
```
jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?user=postgres.abcdefghijk&password=YOUR_PASSWORD_HERE
```

#### 2. Add to Environment Files

```bash
# In ops/environments/dev.env
JDBC_URL=jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?user=postgres.XXXXXX&password=YOUR_DEV_PASSWORD

# In ops/environments/prod.env
JDBC_URL=jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?user=postgres.XXXXXX&password=YOUR_PROD_PASSWORD
```

**Why Session Pooler?**
- Connection pooling handled by Supabase (no need for Hikari config)
- Better performance for serverless/container environments
- Port 6543 (pooled) vs 5432 (direct)

### SSL Certificates

```bash
# Create certs directory
sudo mkdir -p /opt/finpath/certs
sudo chmod 755 /opt/finpath/certs

# Add Cloudflare Origin Certificates (see CLOUDFLARE_SETUP.md)
sudo nano /opt/finpath/certs/dev-cloudflare-cert.pem
sudo nano /opt/finpath/certs/dev-cloudflare-key.pem
sudo nano /opt/finpath/certs/cloudflare-cert.pem
sudo nano /opt/finpath/certs/cloudflare-key.pem

# Secure private keys
sudo chmod 600 /opt/finpath/certs/*.pem
```

### Docker Network Setup

```bash
# Create shared Docker network
docker network create finpath-net
```

### Deploy Both Environments

```bash
# Deploy Development
./scripts/deploy-dev.sh

# Deploy Production
./scripts/deploy-prod.sh
```

---

## 🔄 Updates & Deployments

### Update Development Environment

```bash
cd /opt/finpath/ops
./scripts/deploy-dev.sh
```

### Update Production Environment

```bash
cd /opt/finpath/ops
./scripts/deploy-prod.sh
```

### Manual Deployment

```bash
# Development
cd /opt/finpath/ops
git pull origin dev
docker compose -f docker-compose.dev.yml pull
docker compose -f docker-compose.dev.yml up -d

# Production
cd /opt/finpath/ops
git pull origin main
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

---

## 🔍 Monitoring & Debugging

### Check Status

```bash
# View all running containers
docker ps --filter "name=finpath"

# Check logs
docker logs finpath-backend-dev -f
docker logs finpath-backend-prod -f
docker logs finpath-proxy -f

# Run debug script (auto-detects environment)
./scripts/debug.sh
```

### Health Checks

```bash
# Development
curl https://dev.css-appli24.com/actuator/health

# Production
curl https://css-appli24.com/actuator/health
```

### Access Logs

```bash
# Backend logs
docker logs finpath-backend-dev --tail 100
docker logs finpath-backend-prod --tail 100

# Nginx logs
docker logs finpath-proxy --tail 100
```

---

## 🔒 Security

### Firewall Configuration

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### SSL/TLS

- **Cloudflare Origin Certificates** (not Let's Encrypt)
- TLS 1.2 and 1.3 only
- Strong cipher suites
- HSTS enabled

### Rate Limiting

- **Development**: 30 req/sec, burst 50
- **Production**: 10 req/sec, burst 20

---

## 💾 Database Management

### Access Database

Via Supabase Dashboard:
1. Go to your Supabase project
2. Database → SQL Editor

Via psql (Direct Connection):
```bash
psql "postgresql://postgres.[YOUR_REF]:[PASSWORD]@aws-0-eu-central-1.pooler.supabase.com:5432/postgres"
```

### Test Connection from Backend

```bash
# Check if backend can connect to Supabase
docker exec finpath-backend-dev env | grep JDBC_URL

# View backend logs for connection errors
docker logs finpath-backend-dev --tail 50 | grep -i "database\|connection\|supabase"
```

### Backups

Supabase provides automatic backups. For manual backups:

```bash
# Export
pg_dump "postgresql://postgres.[YOUR_REF]:[PASSWORD]@aws-0-eu-central-1.pooler.supabase.com:5432/postgres" > backup-$(date +%Y%m%d).sql

# Restore
psql "postgresql://postgres.[YOUR_REF]:[PASSWORD]@aws-0-eu-central-1.pooler.supabase.com:5432/postgres" < backup.sql
```

---

## 🆘 Troubleshooting

### Backend Can't Connect to Supabase

```bash
# 1. Check JDBC_URL format
docker exec finpath-backend-dev env | grep JDBC_URL

# Should be: jdbc:postgresql://...pooler.supabase.com:6543/postgres?user=...&password=...

# 2. Check backend logs
docker logs finpath-backend-dev --tail 50

# 3. Test from backend container
docker exec -it finpath-backend-dev sh
# Then try: wget -O- https://aws-0-eu-central-1.pooler.supabase.com

# 4. Verify Supabase project is active
# Check Supabase Dashboard for any outages or issues

# 5. Check if Session Pooler is enabled
# Supabase Dashboard → Settings → Database → Connection pooling
```

### Container Won't Start

```bash
# Check logs
docker logs finpath-backend-dev
docker logs finpath-backend-prod

# Check environment variables
docker exec finpath-backend-dev env | grep JDBC_URL

# Restart container
docker restart finpath-backend-dev
```

### Nginx Issues

```bash
# Check nginx config syntax
docker exec finpath-proxy nginx -t

# Reload nginx
docker exec finpath-proxy nginx -s reload

# Check if containers are reachable
docker exec finpath-proxy wget -qO- http://backend-dev:9080/actuator/health
docker exec finpath-proxy wget -qO- http://backend-prod:9080/actuator/health
```

### Network Issues

```bash
# Inspect network
docker network inspect finpath-net

# Reconnect container to network
docker network disconnect finpath-net finpath-backend-dev
docker network connect finpath-net finpath-backend-dev
```

---

## 🔧 Maintenance

### Update Docker Images

```bash
# Pull latest images
docker compose -f docker-compose.dev.yml pull
docker compose -f docker-compose.prod.yml pull

# Restart with new images
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up -d
```

### Clean Up

```bash
# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune

# Full cleanup (careful!)
docker system prune -a --volumes
```

### Restart Everything

```bash
# Stop all
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.prod.yml down

# Start all
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up -d
```

---

## 📚 Additional Resources

- **Cloudflare Setup**: See `CLOUDFLARE_SETUP.md`
- **GitHub Repository**: https://github.com/JanisHuber/FinPath
- **Supabase Dashboard**: https://app.supabase.com
- **Cloudflare Dashboard**: https://dash.cloudflare.com

---

## 📊 Resource Usage

Recommended Hetzner Server specs for dual-stack:

- **Minimum**: CPX21 (3 vCPU, 4GB RAM, 80GB SSD)
- **Recommended**: CPX31 (4 vCPU, 8GB RAM, 160GB SSD)

Expected resource usage:
- Backend Dev: ~512MB RAM
- Backend Prod: ~768MB RAM
- Frontend Dev: ~100MB RAM
- Frontend Prod: ~100MB RAM
- Nginx: ~50MB RAM
- **Total**: ~2-3GB RAM

---

## ✅ Deployment Checklist

- [ ] Hetzner Server provisioned (Ubuntu 24.04)
- [ ] Docker installed and running
- [ ] Deploy user created with SSH access
- [ ] Repository cloned to `/opt/finpath`
- [ ] Supabase project created
- [ ] Session Pooler enabled in Supabase
- [ ] JDBC URL copied from Supabase
- [ ] `dev.env` and `prod.env` configured with JDBC_URL
- [ ] Cloudflare configured (see CLOUDFLARE_SETUP.md)
- [ ] Origin Certificates stored in `/opt/finpath/certs`
- [ ] Docker network `finpath-net` created
- [ ] Firewall configured (UFW)
- [ ] Development environment deployed
- [ ] Production environment deployed
- [ ] Health checks passing
- [ ] DNS propagated and SSL working

---

**Last Updated**: 2026-01-07
**Maintained by**: Janis Huber
