# Minimales Deployment Setup

Einfaches Deployment ohne HTTPS, SSL-Zertifikate oder Cloudflare.

## Architektur

- **Gemeinsamer Proxy**: Ein einzelner Nginx-Proxy, der unabhängig läuft
- **DEV Environment**: Läuft auf Port 8080
- **PROD Environment**: Läuft auf Port 80
- **Automatisches Deployment**: Push auf `dev` oder `prod` Branch triggert Build & Deploy

## Ersteinrichtung auf Hetzner Server

### 1. Docker Netzwerk erstellen

```bash
docker network create finpath-net
```

### 2. Repository klonen

```bash
cd /opt
git clone https://github.com/janishuber/FinPath.git finpath
cd finpath
```

### 3. Environment-Dateien erstellen

```bash
cd ops/environments

# DEV Environment
cat > dev.env << 'EOF'
DB_URL=jdbc:postgresql://your-db-host:5432/finpath_dev
DB_USERNAME=your-db-user
DB_PASSWORD=your-db-password
EOF

# PROD Environment
cat > prod.env << 'EOF'
DB_URL=jdbc:postgresql://your-db-host:5432/finpath_prod
DB_USERNAME=your-db-user
DB_PASSWORD=your-db-password
EOF
```

### 4. GHCR Login (für private Images)

```bash
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u janishuber --password-stdin
```

### 5. Proxy starten (einmalig)

```bash
cd /opt/finpath/ops
docker compose -f docker-compose.proxy.yml up -d
```

Der Proxy läuft permanent und routet Traffic zu dev/prod Services.

## Deployment Workflow

### Automatisches Deployment

Push auf den entsprechenden Branch:

```bash
# DEV Deployment
git push origin dev

# PROD Deployment
git push origin prod
```

Der GitHub Actions Workflow:
1. Baut die Docker Images
2. Pusht sie zu GHCR
3. Deployed auf den Hetzner Server

### Manuelles Deployment

Falls du manuell deployen möchtest:

```bash
# DEV
cd /opt/finpath/ops
docker compose -f docker-compose.dev.yml pull
docker compose -f docker-compose.dev.yml up -d

# PROD
cd /opt/finpath/ops
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## Zugriff auf die Anwendungen

- **DEV**: http://your-server-ip:8080
- **PROD**: http://your-server-ip

## Nützliche Befehle

### Logs anschauen

```bash
# DEV
docker compose -f docker-compose.dev.yml logs -f

# PROD
docker compose -f docker-compose.prod.yml logs -f

# Proxy
docker compose -f docker-compose.proxy.yml logs -f
```

### Services neustarten

```bash
# DEV
docker compose -f docker-compose.dev.yml restart

# PROD
docker compose -f docker-compose.prod.yml restart

# Proxy
docker compose -f docker-compose.proxy.yml restart
```

### Services stoppen

```bash
# DEV
docker compose -f docker-compose.dev.yml down

# PROD
docker compose -f docker-compose.prod.yml down

# Proxy (normalerweise nicht nötig)
docker compose -f docker-compose.proxy.yml down
```

### Status prüfen

```bash
docker ps
docker network ls
```

### Container Shell öffnen

```bash
# Backend DEV
docker exec -it finpath-backend-dev bash

# Backend PROD
docker exec -it finpath-backend-prod bash

# Proxy
docker exec -it finpath-proxy sh
```

## Troubleshooting

### Services können nicht mit Proxy kommunizieren

Prüfe ob alle Container im gleichen Netzwerk sind:

```bash
docker network inspect finpath-net
```

### Proxy zeigt 502 Bad Gateway

Prüfe ob die Backend/Frontend Services laufen:

```bash
docker ps | grep finpath
```

### Images können nicht gepullt werden

Login erneut bei GHCR:

```bash
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u janishuber --password-stdin
```

### Port bereits in Verwendung

Prüfe welcher Prozess den Port blockiert:

```bash
sudo lsof -i :80
sudo lsof -i :8080
```

## Migration vom alten Setup

Falls du vom alten Setup (mit SSL) migrierst:

```bash
# Alte Services stoppen
cd /opt/finpath/ops
docker compose down

# Alte Nginx-Konfiguration wird automatisch überschrieben
# Proxy neu starten
docker compose -f docker-compose.proxy.yml up -d

# Services neu starten
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up -d
```

## GitHub Secrets

Stelle sicher, dass folgende Secrets in deinem GitHub Repository konfiguriert sind:

- `HETZNER_HOST`: IP-Adresse des Hetzner Servers
- `HETZNER_USER`: SSH Username (z.B. root)
- `HETZNER_SSH_KEY`: Private SSH Key für den Server
