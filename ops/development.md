Projektstruktur

finpath/ <br>
├── finpath-backend/ <br>
├── apps/ <br>
│   ├── finpath-frontend-web/ <br>
│   └── finpath-frontend-mobile/ <br>
└── ops/ <br>
├── compose.dev.yml <br>
├── compose.prod.yml <br>
├── nginx/ <br>
└── secrets/ <br>

⸻

Lokale Entwicklung

Datenbank starten

cd ops
docker compose -f compose.dev.yml up -d db

⸻

Backend starten (Liberty Dev Mode)

DB_HOST=localhost
DB_PORT=5432
DB_NAME=finpath
DB_USER=finpath
DB_PASS=secret
mvn -f ../finpath-backend/pom.xml liberty:dev

Backend erreichbar unter:
http://localhost:9080

⸻

Web-Frontend starten

cd apps/finpath-frontend-web
npm install
npm start

Web-App erreichbar unter:
http://localhost:4200

⸻

Mobile-App (Angular + Capacitor)

Build vorbereiten:

cd apps/finpath-frontend-mobile
npm install
npm run build
npx cap copy

iOS starten:
npm run dev:ios

Android starten:
npm run dev:android

⸻

Optional: Alles über Docker starten

cd ops
docker compose -f compose.dev.yml up -d

Startet automatisch:
•	Postgres
•	Backend
•	Web-Frontend

⸻

Deployment in der Hetzner Cloud

Branch-basierte Deployments
•	Push in den Branch dev → Deployment über compose.dev.yml
•	Push in den Branch prod → Deployment über compose.prod.yml

⸻

Hetzner Server vorbereiten

Repository klonen:

cd /opt
git clone https://github.com/janishuber/finpath.git
cd finpath

Deployment Dev:

git pull
docker compose -f ops/compose.dev.yml up -d

Deployment Prod:

git pull
docker compose -f ops/compose.prod.yml up -d

⸻

Reverse Proxy in Produktion

Ein Reverse Proxy (z. B. Nginx) wird in Produktion für folgende Aufgaben benötigt:
•	HTTPS (Let’s Encrypt)
•	Routing: /api → Backend, / → Frontend
•	Security-Header
•	Rate-Limiting
•	Caching
•	Logging

Konfiguration liegt in ops/nginx/.
