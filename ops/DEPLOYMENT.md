# Minimales Deployment Setup

Einfaches Deployment ohne HTTPS oder SSL-Zertifikate.

## Architektur

- **Gemeinsamer Reverse Proxy**: Ein Nginx-Proxy routet basierend auf Domain-Namen
- **Domain-basiertes Routing**:
  - `dev.css-appli24.com` → DEV Environment
  - `css-appli24.com` → PROD Environment
- **Ein Port (80)**: Alle Anfragen kommen über Port 80
- **Shared Network**: Alle Container im gleichen `finpath-net` Netzwerk
- **Unabhängige Deployments**: DEV und PROD können separat deployed werden
- **Automatisches Deployment**: Push auf `dev` oder `prod` Branch triggert Build & Deploy

## Ersteinrichtung auf Hetzner Server

### 1. DNS-Einträge konfigurieren

Stelle sicher, dass deine Domains auf die Hetzner Server IP zeigen:

```
A Record: dev.css-appli24.com → <HETZNER_IP>
A Record: css-appli24.com → <HETZNER_IP>
A Record: www.css-appli24.com → <HETZNER_IP>
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

### 4. GHCR Login (für private Images)

```bash
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u janishuber --password-stdin
```

### 5. Docker-Netzwerk erstellen

```bash
docker network create finpath-net
```

### 6. Proxy starten (einmalig)

```bash
cd /opt/finpath/ops
docker compose -f docker-compose.proxy.yml up -d
```

Der Proxy läuft permanent und routet Traffic basierend auf dem Domain-Namen.

### 7. Environments starten

```bash
# DEV Environment starten
docker compose -f docker-compose.dev.yml up -d

# PROD Environment starten
docker compose -f docker-compose.prod.yml up -d
```

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

- **DEV**: http://dev.css-appli24.com
- **PROD**: http://css-appli24.com
- **Direct IP**: Wird automatisch auf PROD weitergeleitet

## Nützliche Befehle

### Logs anschauen

```bash
# DEV (Backend und Frontend)
docker compose -f docker-compose.dev.yml logs -f

# PROD (Backend und Frontend)
docker compose -f docker-compose.prod.yml logs -f

# Proxy
docker compose -f docker-compose.proxy.yml logs -f
docker logs finpath-proxy -f
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

### Nginx-Konfiguration neu laden

```bash
# Nach Änderungen an nginx.conf
docker exec finpath-proxy nginx -t  # Konfiguration testen
docker exec finpath-proxy nginx -s reload  # Neu laden ohne Downtime
```

### Services stoppen

```bash
# DEV stoppen (Proxy läuft weiter)
docker compose -f docker-compose.dev.yml down

# PROD stoppen (Proxy läuft weiter)
docker compose -f docker-compose.prod.yml down

# Proxy stoppen (selten nötig)
docker compose -f docker-compose.proxy.yml down
```

### Status prüfen

```bash
# Alle Container anzeigen
docker ps

# Nur DEV Container
docker ps | grep dev

# Nur PROD Container
docker ps | grep prod

# Netzwerk inspizieren
docker network inspect finpath-net
```

### Container Shell öffnen

```bash
# Backend
docker exec -it finpath-backend-dev bash
docker exec -it finpath-backend-prod bash

# Proxy
docker exec -it finpath-proxy sh
```

## Troubleshooting

### 502 Bad Gateway

Prüfe ob die Backend/Frontend Services laufen:

```bash
docker ps | grep finpath
```

Prüfe Proxy-Logs:

```bash
docker logs finpath-proxy --tail 50
```

Wenn ein Service fehlt, starte ihn:

```bash
docker compose -f docker-compose.dev.yml up -d
```

### Domain wird nicht gefunden

Prüfe DNS-Einträge:

```bash
nslookup dev.css-appli24.com
nslookup css-appli24.com
```

Teste den Proxy direkt mit Host-Header:

```bash
curl -H "Host: dev.css-appli24.com" http://localhost
```

### Images können nicht gepullt werden

Login erneut bei GHCR:

```bash
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u janishuber --password-stdin
```

### Nginx-Konfiguration testen

```bash
docker exec finpath-proxy nginx -t
```

### Port 80 bereits in Verwendung

Prüfe welcher Prozess den Port blockiert:

```bash
sudo lsof -i :80
```

Stoppe alte Services:

```bash
docker stop finpath-proxy-dev finpath-proxy-prod 2>/dev/null || true
```

### Container kann Netzwerk nicht joinen

Prüfe ob Netzwerk existiert:

```bash
docker network ls | grep finpath-net
```

Falls nicht, erstelle es:

```bash
docker network create finpath-net
```

### Kompletter Neustart

```bash
cd /opt/finpath/ops

# Alles stoppen
docker compose -f docker-compose.proxy.yml down
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.prod.yml down

# Alles neu starten
docker compose -f docker-compose.proxy.yml up -d
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up -d
```

## Migration vom alten Setup

Falls du vom Port-basierten Setup migrierst:

```bash
cd /opt/finpath/ops

# Alte Services stoppen
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.prod.yml down

# Alte Netzwerke entfernen
docker network rm finpath-net-dev finpath-net-prod 2>/dev/null || true

# Neues Netzwerk erstellen
docker network create finpath-net

# Code aktualisieren
cd /opt/finpath
git fetch origin dev
git reset --hard origin/dev

# Neue Services starten
cd ops
docker compose -f docker-compose.proxy.yml up -d
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up -d
```

## Vorteile dieser Architektur

1. **Standard Port 80**: Kein :8080 in URLs nötig
2. **Domain-basiertes Routing**: Professionell und flexibel
3. **Ein Proxy**: Einfacher zu verwalten, eine Konfiguration
4. **Shared Network**: Effiziente Ressourcennutzung
5. **Unabhängige Deployments**: DEV und PROD können separat aktualisiert werden
6. **Skalierbar**: Einfach weitere Environments hinzufügen (staging, qa, etc.)
7. **Keine Port-Konflikte**: Alle internen Services nutzen expose statt ports
8. **Bereit für HTTPS**: Später einfach Let's Encrypt hinzufügen

## HTTPS später hinzufügen (Optional)

Wenn du später HTTPS möchtest, kannst du Certbot nutzen:

```bash
# Certbot installieren
apt-get update
apt-get install certbot python3-certbot-nginx

# Zertifikate holen (Proxy muss laufen)
certbot --nginx -d css-appli24.com -d www.css-appli24.com -d dev.css-appli24.com

# Auto-Renewal aktivieren
certbot renew --dry-run
```

## GitHub Secrets

Stelle sicher, dass folgende Secrets in deinem GitHub Repository konfiguriert sind:

- `HETZNER_HOST`: IP-Adresse des Hetzner Servers
- `HETZNER_USER`: SSH Username (z.B. root)
- `HETZNER_SSH_KEY`: Private SSH Key für den Server
