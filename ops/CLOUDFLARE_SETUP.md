# Cloudflare Setup Guide für FinPath

Cloudflare dient als CDN, DDoS-Schutz und SSL/TLS-Proxy vor Ihrem Hetzner Server.

---

## 🌐 Architektur mit Cloudflare

```
Benutzer
   │
   ├─→ HTTPS Request zu dev.css-appli24.com
   │
   ▼
Cloudflare (Edge Network)
   │
   ├─ DNS Auflösung
   ├─ DDoS Schutz
   ├─ SSL/TLS Termination (Benutzer ↔ Cloudflare)
   ├─ Web Application Firewall (WAF)
   ├─ Caching (statische Assets)
   ├─ Rate Limiting (optional)
   │
   ▼
Cloudflare Origin Certificate (verschlüsselt)
   │
   ▼
Hetzner Server (HTTPS mit Origin Cert)
   │
   ├─ Nginx (Reverse Proxy)
   │  ├─ SSL/TLS mit Origin Certificate
   │  ├─ Rate Limiting (Backend-spezifisch)
   │  └─ Routing
   │
   ├─→ Frontend Container (Angular)
   └─→ Backend Container (Spring Boot)
          │
          └─→ Supabase (PostgreSQL)
```

---

## 📋 Was übernimmt Cloudflare?

### ✅ Von Cloudflare übernommen:

1. **DNS Management**
   - Nameserver für Ihre Domain
   - DNS-Einträge für Subdomains

2. **SSL/TLS Verschlüsselung (Client → Cloudflare)**
   - Automatisches SSL-Zertifikat für Endbenutzer
   - Unterstützt alle modernen Browser
   - Automatische Erneuerung

3. **DDoS-Schutz**
   - Layer 3/4/7 DDoS Protection
   - Automatische Abwehr von Angriffen
   - Kostenlos im Free Plan

4. **Content Delivery Network (CDN)**
   - Caching von statischen Assets
   - Schnellere Ladezeiten weltweit
   - Reduziert Server-Last

5. **Web Application Firewall (WAF)**
   - Schutz vor OWASP Top 10
   - Managed Rulesets (Pro Plan+)
   - Custom Rules möglich

6. **Rate Limiting** (optional, Pro Plan+)
   - Globales Rate Limiting
   - Schutz vor Brute-Force

7. **Analytics**
   - Traffic-Statistiken
   - Threat-Analytics
   - Performance-Metriken

### ⚠️ Bleibt auf Ihrem Server:

1. **Origin SSL/TLS (Cloudflare → Server)**
   - Nginx verwaltet Origin Certificates
   - Rate Limiting für API-Endpoints

2. **Application Logic**
   - Backend-Routing
   - API-Rate-Limiting (feingranular)
   - Health Checks

3. **Database**
   - Supabase (extern)

---

## 🚀 Schritt-für-Schritt Setup

### 1. Domain zu Cloudflare hinzufügen

```
1. Gehen Sie zu: https://dash.cloudflare.com
2. Klicken Sie auf "+ Add a Site"
3. Geben Sie Ihre Domain ein: css-appli24.com
4. Wählen Sie den "Free" Plan (ausreichend für Start)
5. Cloudflare scannt automatisch Ihre DNS-Einträge
```

### 2. Nameserver bei Domain-Registrar ändern

```
Cloudflare zeigt Ihnen 2 Nameserver an, z.B.:
- alice.ns.cloudflare.com
- bob.ns.cloudflare.com

Bei Ihrem Domain-Registrar (z.B. Namecheap, GoDaddy, etc.):
1. Gehen Sie zu DNS-Einstellungen
2. Ändern Sie die Nameserver auf die von Cloudflare
3. Speichern (kann 24-48h dauern)
```

**Wichtig:** Nach der Änderung warten Sie auf die Bestätigung von Cloudflare.

### 3. DNS-Einträge konfigurieren

In Cloudflare Dashboard → DNS → Records:

#### A-Records (IPv4):

| Type | Name | Content (IPv4) | Proxy Status | TTL |
|------|------|----------------|--------------|-----|
| A | css-appli24.com | `IHRE_SERVER_IP` | 🟠 Proxied | Auto |
| A | dev | `IHRE_SERVER_IP` | 🟠 Proxied | Auto |
| A | www | `IHRE_SERVER_IP` | 🟠 Proxied | Auto |

**Proxy Status**:
- 🟠 **Proxied** (Orange Cloud) = Traffic geht durch Cloudflare (empfohlen)
- ⚪ **DNS Only** (Grey Cloud) = Direct Connection zum Server

**Empfehlung:** Immer Proxied für Webseiten verwenden!

#### AAAA-Records (IPv6, optional):

Falls Ihr Hetzner Server IPv6 hat:

| Type | Name | Content (IPv6) | Proxy Status | TTL |
|------|------|----------------|--------------|-----|
| AAAA | css-appli24.com | `IHRE_IPV6` | 🟠 Proxied | Auto |
| AAAA | dev | `IHRE_IPV6` | 🟠 Proxied | Auto |

### 4. SSL/TLS Konfiguration

#### 4.1 SSL/TLS Mode einstellen

```
Cloudflare Dashboard → SSL/TLS → Overview
```

Wählen Sie: **Full (strict)** ✅

**SSL/TLS Modes erklärt:**

- ❌ **Off**: Kein SSL (nie verwenden!)
- ⚠️ **Flexible**: SSL nur zwischen Benutzer ↔ Cloudflare (unsicher!)
- ✅ **Full**: SSL auf beiden Seiten, aber nicht validiert
- ✅ **Full (strict)**: SSL auf beiden Seiten mit Validierung (EMPFOHLEN)

**Full (strict)** bedeutet:
- Benutzer → Cloudflare: SSL mit Cloudflare Zertifikat
- Cloudflare → Server: SSL mit validiertem Origin Certificate

#### 4.2 Origin Certificates generieren

```
Cloudflare Dashboard → SSL/TLS → Origin Server
→ Create Certificate
```

**Einstellungen:**
- Private key type: `RSA (2048)`
- Hostnames:
  ```
  css-appli24.com
  *.css-appli24.com
  ```
  (Wildcard deckt dev.css-appli24.com ab)
- Certificate Validity: `15 years` (empfohlen)

**Download:**
1. **Origin Certificate** (`.pem` Format) → Speichern
2. **Private Key** (`.pem` Format) → Speichern

**Für Production:**
- Certificate → `cloudflare-cert.pem`
- Private Key → `cloudflare-key.pem`

**Für Development:**
Sie können entweder:
- Dasselbe Certificate verwenden (Wildcard deckt ab), oder
- Separates Certificate nur für `dev.css-appli24.com` erstellen

#### 4.3 Certificates auf Server speichern

```bash
# SSH zum Server
ssh deploy@IHRE_SERVER_IP

# Certificates speichern
sudo mkdir -p /opt/finpath/certs
sudo chmod 755 /opt/finpath/certs

# Production Certificate
sudo nano /opt/finpath/certs/cloudflare-cert.pem
# Paste Origin Certificate

sudo nano /opt/finpath/certs/cloudflare-key.pem
# Paste Private Key

# Für Dev (falls separates Cert):
sudo nano /opt/finpath/certs/dev-cloudflare-cert.pem
sudo nano /opt/finpath/certs/dev-cloudflare-key.pem

# ODER: Symlinks verwenden (wenn dasselbe Cert):
sudo ln -s /opt/finpath/certs/cloudflare-cert.pem /opt/finpath/certs/dev-cloudflare-cert.pem
sudo ln -s /opt/finpath/certs/cloudflare-key.pem /opt/finpath/certs/dev-cloudflare-key.pem

# Permissions
sudo chmod 600 /opt/finpath/certs/*-key.pem
sudo chmod 644 /opt/finpath/certs/*-cert.pem
```

#### 4.4 docker-compose.prod.yml aktualisieren

Die nginx.conf referenziert bereits die Certificates:

```yaml
# In docker-compose.prod.yml
reverse-proxy:
  volumes:
    - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
    - /opt/finpath/certs:/etc/nginx/certs:ro  # Mount certificates
```

### 5. Cloudflare Security Features aktivieren

#### 5.1 Always Use HTTPS

```
SSL/TLS → Edge Certificates → Always Use HTTPS: ON ✅
```

Forciert HTTPS für alle Requests.

#### 5.2 Automatic HTTPS Rewrites

```
SSL/TLS → Edge Certificates → Automatic HTTPS Rewrites: ON ✅
```

Konvertiert HTTP-Links in HTTPS automatisch.

#### 5.3 Minimum TLS Version

```
SSL/TLS → Edge Certificates → Minimum TLS Version: TLS 1.2 ✅
```

#### 5.4 Security Level

```
Security → Settings → Security Level: Medium ✅
```

**Optionen:**
- **Essentially Off**: Nur bei extremen Angriffen
- **Low**: Nur bekannt schädliche IPs
- **Medium**: Empfohlen für die meisten Sites
- **High**: Challenges bei verdächtigem Traffic
- **I'm Under Attack**: Alle Besucher müssen Challenge lösen

### 6. Caching Konfiguration

#### 6.1 Caching Level

```
Caching → Configuration → Caching Level: Standard ✅
```

#### 6.2 Browser Cache TTL

```
Caching → Configuration → Browser Cache TTL: 4 hours
```

#### 6.3 Page Rules für API-Caching

```
Rules → Page Rules → Create Page Rule
```

**Rule 1: API nicht cachen**
- URL: `*css-appli24.com/api/*`
- Settings:
  - Cache Level: `Bypass`
  - Disable Performance

**Rule 2: API nicht cachen (Dev)**
- URL: `*dev.css-appli24.com/api/*`
- Settings:
  - Cache Level: `Bypass`
  - Disable Performance

**Rule 3: Static Assets cachen**
- URL: `*css-appli24.com/*.js`
- Settings:
  - Browser Cache TTL: `1 day`
  - Cache Level: `Cache Everything`

(Wiederholen für `*.css`, `*.png`, `*.jpg`, `*.svg`, etc.)

### 7. Firewall Rules (optional, aber empfohlen)

```
Security → WAF → Firewall Rules
```

**Rule 1: Block Bad Bots**
```
Expression: (cf.client.bot)
Action: Challenge
```

**Rule 2: Geo-Blocking (optional)**
Falls Sie nur bestimmte Länder zulassen wollen:
```
Expression: (ip.geoip.country ne "DE" and ip.geoip.country ne "CH" and ip.geoip.country ne "AT")
Action: Block or Challenge
```

**Rule 3: Rate Limiting (Free Tier hat Limits)**
```
Expression: (http.request.uri.path contains "/api/")
Action: Challenge
Rate: 10 requests per 10 seconds
```

### 8. Performance Optimierungen

#### 8.1 Auto Minify

```
Speed → Optimization → Auto Minify
```

Aktivieren für:
- ✅ JavaScript
- ✅ CSS
- ✅ HTML

#### 8.2 Brotli Compression

```
Speed → Optimization → Brotli: ON ✅
```

#### 8.3 Early Hints (Free)

```
Speed → Optimization → Early Hints: ON ✅
```

#### 8.4 HTTP/3 (QUIC)

```
Network → HTTP/3 (with QUIC): ON ✅
```

---

## ✅ Verifikation

### 1. DNS propagiert?

```bash
# Auf lokalem Rechner
nslookup css-appli24.com
nslookup dev.css-appli24.com

# Sollte Cloudflare IPs zeigen, nicht Ihre Server-IP
```

### 2. SSL funktioniert?

```bash
# Test HTTPS Verbindung
curl -I https://css-appli24.com
curl -I https://dev.css-appli24.com

# Überprüfe Zertifikat
openssl s_client -connect css-appli24.com:443 -servername css-appli24.com
```

### 3. Cloudflare ist aktiv?

```bash
# Response Header sollten "cf-ray" enthalten
curl -I https://css-appli24.com | grep cf-ray

# Oder
curl -I https://css-appli24.com | grep -i cloudflare
```

### 4. Origin Certificate auf Server?

```bash
# Auf Server
sudo ls -la /opt/finpath/certs/

# Test Nginx Config
docker exec finpath-proxy nginx -t
```

### 5. Backend erreichbar?

```bash
# Von außen
curl https://css-appli24.com/actuator/health
curl https://dev.css-appli24.com/actuator/health
```

---

## 🔍 Troubleshooting

### Problem: 521 Error (Web server is down)

**Ursache:** Cloudflare kann Server nicht erreichen

**Lösung:**
```bash
# 1. Ist Server erreichbar?
ping IHRE_SERVER_IP

# 2. Läuft Nginx?
docker ps | grep finpath-proxy

# 3. Firewall prüfen
sudo ufw status

# 4. SSL auf Server korrekt?
docker logs finpath-proxy
```

### Problem: 525 Error (SSL handshake failed)

**Ursache:** Origin Certificate Problem

**Lösung:**
```bash
# 1. Nginx Config testen
docker exec finpath-proxy nginx -t

# 2. Certificates vorhanden?
sudo ls -la /opt/finpath/certs/

# 3. Certificates gemountet?
docker inspect finpath-proxy | grep -A 5 "Mounts"

# 4. SSL/TLS Mode in Cloudflare: Full (strict)?
```

### Problem: 522 Error (Connection timed out)

**Ursache:** Server antwortet nicht

**Lösung:**
```bash
# 1. Server erreichbar?
telnet IHRE_SERVER_IP 443

# 2. Nginx läuft?
docker logs finpath-proxy --tail 50

# 3. Backend läuft?
docker logs finpath-backend-prod --tail 50
```

### Problem: Infinite Redirect Loop

**Ursache:** SSL/TLS Mode falsch

**Lösung:**
- Cloudflare SSL/TLS Mode: **Full (strict)**
- Nginx in docker-compose muss Port 443 mappen
- Origin Certificate korrekt installiert

---

## 📊 Monitoring & Analytics

### Cloudflare Dashboard

```
Analytics → Traffic
```

Zeigt:
- Requests pro Zeit
- Bandwidth
- Cached vs. Uncached Requests
- HTTP Status Codes
- Top Countries

### Threat Analysis

```
Security → Analytics
```

Zeigt:
- Blocked Requests
- Challenge Success Rate
- Bot Traffic

---

## 💰 Free vs. Pro Plan

### Free Plan (ausreichend für Start):
- ✅ Unlimited DDoS Protection
- ✅ SSL/TLS (Universal SSL)
- ✅ CDN (globales Netzwerk)
- ✅ Web Application Firewall (Basic)
- ✅ 3 Page Rules
- ✅ Basic Analytics

### Pro Plan ($20/Monat, optional):
- ✅ Alle Free Features
- ✅ 20 Page Rules
- ✅ WAF Managed Rulesets
- ✅ Advanced Analytics
- ✅ Image Optimization
- ✅ Mobile Redirect

**Empfehlung für Start:** Free Plan reicht völlig aus!

---

## 🎯 Checkliste

- [ ] Domain zu Cloudflare hinzugefügt
- [ ] Nameserver geändert (bei Registrar)
- [ ] DNS-Einträge konfiguriert (A-Records proxied)
- [ ] SSL/TLS Mode: Full (strict)
- [ ] Origin Certificates generiert
- [ ] Certificates auf Server gespeichert
- [ ] Always Use HTTPS: ON
- [ ] Security Level: Medium
- [ ] Page Rules für API (Bypass Cache)
- [ ] Auto Minify aktiviert
- [ ] HTTP/3 aktiviert
- [ ] Verifikation: DNS, SSL, Cloudflare-Header
- [ ] Backend Health Check funktioniert

---

**Setup-Zeit:** ca. 30-60 Minuten (inkl. DNS-Propagation)

**Bei Fragen:** Cloudflare Community: https://community.cloudflare.com
