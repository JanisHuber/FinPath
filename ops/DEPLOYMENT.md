# Minimales Deployment Setup

Einfaches Deployment ohne HTTPS, SSL-Zertifikate oder Cloudflare.

## Architektur

- **Separate Proxies**: Jedes Environment hat seinen eigenen Nginx-Proxy
- **DEV Environment**: Eigenes Netzwerk, läuft auf Port 8080
- **PROD Environment**: Eigenes Netzwerk, läuft auf Port 80
- **Automatisches Deployment**: Push auf `dev` oder `prod` Branch triggert Build & Deploy
- **Vollständige Isolation**: Dev und Prod sind komplett unabhängig

## Ersteinrichtung auf Hetzner Server

### 1. Repository klonen

```bash
cd /opt
git clone https://github.com/janishuber/FinPath.git finpath
cd finpath
```

### 2. Environment-Dateien erstellen

```bash
cd ops/environments

# DEV Environment
cat > dev.env << 'EOF'
JDBC_URL=jdbc:postgresql://your-db-host:5432/finpath_dev
DB_USERNAME=your-db-user
DB_PASSWORD=your-db-password
EOF

# PROD Environment
cat > prod.env << 'EOF'
JDBC_URL=jdbc:postgresql://your-db-host:5432/finpath_prod
DB_USERNAME=your-db-user
DB_PASSWORD=your-db-password
EOF
```

### 3. GHCR Login (für private Images)

```bash
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u janishuber --password-stdin
```

### 4. Services starten

```bash
cd /opt/finpath/ops

# DEV Environment starten
docker compose -f docker-compose.dev.yml up -d

# PROD Environment starten (optional, unabhängig von DEV)
docker compose -f docker-compose.prod.yml up -d
```

Jedes Environment:
- Erstellt sein eigenes Docker-Netzwerk automatisch
- Startet seinen eigenen Proxy
- Ist komplett unabhängig vom anderen Environment

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
3. Deployed auf den Hetzner Server (nur das jeweilige Environment)

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
# DEV (alle Services inkl. Proxy)
docker compose -f docker-compose.dev.yml logs -f

# PROD (alle Services inkl. Proxy)
docker compose -f docker-compose.prod.yml logs -f

# Nur Proxy
docker logs finpath-proxy-dev -f
docker logs finpath-proxy-prod -f
```

### Services neustarten

```bash
# DEV (inkl. Proxy)
docker compose -f docker-compose.dev.yml restart

# PROD (inkl. Proxy)
docker compose -f docker-compose.prod.yml restart

# Nur Proxy neustarten
docker restart finpath-proxy-dev
docker restart finpath-proxy-prod
```

### Services stoppen

```bash
# DEV komplett stoppen
docker compose -f docker-compose.dev.yml down

# PROD komplett stoppen
docker compose -f docker-compose.prod.yml down
```

### Status prüfen

```bash
# Alle Container anzeigen
docker ps

# Nur DEV Container
docker ps | grep dev

# Nur PROD Container
docker ps | grep prod

# Netzwerke anzeigen
docker network ls
```

### Container Shell öffnen

```bash
# Backend
docker exec -it finpath-backend-dev bash
docker exec -it finpath-backend-prod bash

# Proxy
docker exec -it finpath-proxy-dev sh
docker exec -it finpath-proxy-prod sh
```

## Troubleshooting

### Proxy zeigt 502 Bad Gateway

Prüfe ob die Backend/Frontend Services laufen:

```bash
# DEV
docker compose -f docker-compose.dev.yml ps

# PROD
docker compose -f docker-compose.prod.yml ps
```

Wenn Services nicht laufen, neu starten:

```bash
docker compose -f docker-compose.dev.yml restart
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

Falls alte Container laufen:

```bash
# Alten Proxy stoppen
docker stop finpath-proxy
docker rm finpath-proxy

# Altes Netzwerk entfernen
docker network rm finpath-net
```

### Nginx-Konfiguration testen

```bash
# DEV Proxy Config testen
docker exec finpath-proxy-dev nginx -t

# PROD Proxy Config testen
docker exec finpath-proxy-prod nginx -t
```

### Kompletter Neustart eines Environments

```bash
# DEV komplett neu aufsetzen
cd /opt/finpath/ops
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.dev.yml pull
docker compose -f docker-compose.dev.yml up -d
```

## Migration vom alten Setup

Falls du vom alten Setup (mit gemeinsamem Proxy) migrierst:

```bash
# Alle alten Services stoppen
cd /opt/finpath/ops
docker compose -f docker-compose.proxy.yml down 2>/dev/null || true
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.prod.yml down

# Alte Container und Netzwerke aufräumen
docker rm -f finpath-proxy 2>/dev/null || true
docker network rm finpath-net 2>/dev/null || true

# Neue Services starten
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up -d
```

## Vorteile dieser Architektur

1. **Unabhängigkeit**: DEV kann down sein ohne PROD zu beeinflussen
2. **Isolation**: Separate Netzwerke verhindern versehentliche Kommunikation
3. **Einfaches Debugging**: Jeder Proxy hat nur seine eigenen Upstreams
4. **Flexibilität**: Du kannst nur DEV oder nur PROD laufen lassen
5. **Klare Trennung**: Keine geteilten Ressourcen zwischen Environments

## GitHub Secrets

Stelle sicher, dass folgende Secrets in deinem GitHub Repository konfiguriert sind:

- `HETZNER_HOST`: IP-Adresse des Hetzner Servers
- `HETZNER_USER`: SSH Username (z.B. root)
- `HETZNER_SSH_KEY`: Private SSH Key für den Server
