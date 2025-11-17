Perfekt 🔥 — das ist genau der richtige Moment, um deine Vision sauber für Copilot zu „grounden“.
Hier ist dein vollständiges .github/copilot-instructions.md,
voll abgestimmt auf FinGuide, deinen Stack (Angular + Jakarta EE + Postgres), dein Design-Ziel (Revolut-Look)
und deine Vision (intelligenter, kontextbewusster Schweizer Finanzcoach mit Bildung, Motivation und Umsetzung).

⸻

.github/copilot-instructions.md

# 🧭 Copilot Instructions — FinGuide

## 🪙 Projektüberblick
**FinGuide** ist eine digitale All-in-One-Finanzplattform für die **Schweiz**, die **Finanzbildung, Analyse, Handlungsempfehlungen und Zielplanung** in einem modernen, interaktiven Interface vereint.  
Ziel: Schweizerinnen und Schweizer sollen ihre Finanzen **verstehen, optimieren und automatisiert verbessern** können – ähnlich wie Revolut oder Yuh, aber mit Fokus auf **Finanzkompetenz und langfristige Planung**.

---

## 🎯 Vision & Ziele

### Hauptziele
1. **KI-basierte Finanzbegleitung** – Ein digitaler Coach, der Kontext versteht, Muster erkennt und Handlungstipps gibt („Zahle in Säule 3a ein, um Steuern zu sparen“).
2. **Finanzübersicht** – Saubere, interaktive Dashboards zu Liquidität, Säule 3a, BVG, Investments, Ausgaben & Budget.
3. **Lernplattform** – Interaktive Lernmodule (z. B. „Budgetieren“, „Steuern verstehen“, „Investieren Basics“) mit Fortschrittsanzeige.
4. **Zielplanung (Finanzfahrplan)** – Persönliche Ziele (Notgroschen, Eigenheim, Rente) mit Fortschritts- und Zeitachse.
5. **Motivation & Nudging** – Smart Notifications: FinGuide gibt kontextuelle Vorschläge wie ein persönlicher Finanzcoach.
6. **Datensicherheit** – Alle Berechnungen und KI-Empfehlungen laufen lokal oder über gesicherte APIs. Keine sensiblen Daten werden extern gespeichert.

---

## 🧩 Architektur

### Frontend
- **Framework:** Angular 20 (Standalone Components)
- **Styling:** Tailwind CSS, moderne Fintech-Optik (Revolut-ähnlich, clean, hell, mit Akzentfarben #16A34A und #111827)
- **Mocking:** MSW (Mock Service Worker) für API-Simulation
- **Charts:** Recharts oder ApexCharts (einfache, aussagekräftige Diagramme)
- **Build-Tools:** Angular CLI + Vite
- **Dateistruktur (Best Practice):**

src/
├─ app/
│  ├─ core/           # globale Services, Guards, Interceptors
│  ├─ shared/         # wiederverwendbare UI-Komponenten
│  │  ├─ ui/header/
│  │  └─ ui/sidebar/
│  ├─ features/
│  │  ├─ dashboard/
│  │  ├─ finance/
│  │  ├─ learning/
│  │  ├─ settings/
│  │  │  ├─ profile/
│  │  │  ├─ appearance/
│  │  │  └─ security/
│  │  └─ goals/
│  └─ app.routes.ts
├─ assets/
└─ environments/

- **Routing-Prinzip:**
- Haupt-Routen: `/dashboard`, `/finance`, `/learning`, `/goals`, `/settings`
- **Child Routes** bei Settings (z. B. `/settings/profile`, `/settings/appearance`)
- Lazy-Loading per `loadComponent` oder `loadChildren`

- **Komponenten-Naming:**
- UI-Komponenten → `*.component.ts`
- Seiten → `*.page.ts`
- Selektoren → `app-*`
- CSS/HTML gleichnamig (`*.component.css`, `*.component.html`)

- **Layout:**
- Sticky Header + Sidebar
- Responsive Design (mobile = Bottom Nav, Desktop = Sidebar)
- Dark-Mode via CSS-Var und Tailwind-Plugin

---

### Backend
- **Runtime:** Open Liberty 25 (Jakarta EE 10, MicroProfile 7)
- **Sprache:** Java 21+
- **Features:** webProfile-10.0, jdbc-4.3, jsonb-3.0, restfulWS-3.1, mpHealth-4.0
- **API:** REST (JSON) → z. B. `/api/finance`, `/api/goals`, `/api/recommendations`
- **Database:** PostgreSQL 16 über JDBC (`jdbc/FinPathDS`)
- **Deployment:** Docker + docker-compose.dev.yml
- **Struktur:**

src/
├─ main/java/ch/finpath/rest/
├─ main/resources/META-INF/persistence.xml
└─ main/liberty/config/server.xml

---

## 💬 KI-Coach (Gemini / lokales LLM)
- **Zweck:** Generiert Handlungsempfehlungen basierend auf Benutzer-, Finanz- und Lernstatus.
- **Format:** JSON-Output → Frontend zeigt Empfehlungen im Dashboard an.
- **Kontext:** Schweizer Finanzsystem (Säule 3a, BVG, Steuern, ETF-Sparen)
- **Prinzip:** Keine Finanz- oder Steuerberatung, nur plausible, allgemeine Tipps.

Beispiel-Output:
```json
{
"recommendations": [
  {
    "title": "Säule 3a optimieren",
    "message": "Du hast noch 5'800 CHF frei – nutze den Steuervorteil.",
    "category": "Steuern & Vorsorge",
    "priority": "high",
    "action_suggestion": "Zahle bis Jahresende ein."
  }
]
}


⸻

🔐 Sicherheitsprinzipien
	1.	Keine echten Nutzerdaten im Frontend – alles Mock bis Backend-Anbindung.
	2.	HTTPS, CORS, CSRF-Schutz aktivieren im Liberty-Server.
	3.	LLM-Isolation: AI-Berechnungen lokal oder über gesicherte Backend-Route.
	4.	Sensitive Values: .env-Files nicht committen (→ docker-compose bindet sie ein).
	5.	Role Future: OAuth2 / Keycloak möglich, aber nicht im MVP.

⸻

📊 Dashboard & Finance UI Guidelines
	•	Visuelle Sprache: modern, clean, minimalistisch (Revolut-Look & Feel)
	•	Hauptabschnitte:
	•	Liquidität: Kontostände, Cashflow
	•	Säule 3a: Einzahlungen, Steuerersparnis, Fortschritt
	•	BVG: Arbeitgeberbeiträge, Prognose
	•	Investments: ETFs, Rendite, Risikoprofil
	•	Ziele: Notgroschen, Eigenheim, Pension
	•	Lernen: Fortschritt, nächste Empfehlung
	•	Diagramme:
	•	Donut-Charts (Verteilung Vermögen)
	•	Line-Charts (Budget-Entwicklung über Zeit)
	•	Bar-Charts (Einzahlungen / Ausgaben)
	•	Interaktion:
	•	Tooltipps + Hover-Effekte für mehr Infos
	•	Inline-Editing (bei Budget oder Zielen)
	•	“Quick Actions” im Dashboard („+ 3a-Einzahlung“, „Lernmodul fortsetzen“)

⸻

⚙️ Entwicklungsprinzipien
	•	Code Quality: Saubere Imports, Klassenbenennung nach Angular Style Guide.
	•	TypeScript: Strict Mode aktiv.
	•	Responsiveness: Tailwind Utilities, keine Hardcoded Größen.
	•	Tests: Unit-Tests mit Jest / Vitest, Mocking über MSW.
	•	Dokumentation: Jede Page und jeder Service kurz im Code kommentieren.
	•	Git Flow: main = stable, dev = Integration, feature/ = Tasks.
	•	Commits: conventional-commit Style (feat:, fix:, chore:).

⸻

🧠 Copilot-Erwartung

Copilot soll:
	•	Vorschläge im Stil von Revolut, Yuh oder Finary gestalten.
	•	Immer UX-flussorientiert denken (Dashboard → Analyse → Empfehlung → Aktion).
	•	Technisch präzise, keine Abkürzungen, keine Inline-SQL.
	•	Sicherheitsaspekte beachten, aber Mockdaten zulassen (z. B. MSW-Handler).
	•	Bei jeder neuen Komponente automatisch Tailwind-Utility-Klassen nutzen.
	•	Sprachlich Deutsch (Du-Form), aber Code und Kommentare auf Englisch.
	•	Wenn nicht sicher: Fragen stellen statt raten.

⸻

🧩 Beispiel-User (Datenmodell)

{
  "id": 1001,
  "name": "Luca Meier",
  "age": 28,
  "location": "Luzern",
  "employment": { "status": "Angestellt", "salary": 7200 },
  "accounts": { "checking": 5400, "savings": 8200, "pillar3a": 12500, "investments": 7400 },
  "expenses": { "total": 3400 },
  "goals": [
    { "name": "Notgroschen", "target": 21000, "current": 8200 },
    { "name": "ETF-Sparplan", "target": 50000, "current": 7400 }
  ],
  "learning_progress": { "modules_completed": 3, "total": 10 }
}


⸻

🧱 UX-Flow (High-Level)
	1.	Dashboard: Übersicht + Empfehlungen des KI-Coaches.
	2.	FinancePath: Details zu Liquidität, Säule 3a, Investments.
	3.	Learning: Lernmodule mit Fortschritt.
	4.	Goals: Finanzziele mit Zeitleiste.
	5.	Settings: Tabs → Profil / App-Darstellung / Sicherheit.

⸻

🔄 Entwicklungs-Flow
	1.	Frontend zuerst: UI designen mit Mockdaten (MSW).
	2.	Backend stubben: Jakarta EE REST Endpoints mit Postgres.
	3.	Integration: Frontend calls Backend → echte API.
	4.	KI & Empfehlungen: LLM in Backend einbetten.
	5.	Optimierung: Charts, UX, Performance, Security.

⸻

✅ Kommunikationsstil
	•	Knappe, klare Anweisungen.
	•	Fachlich präzise Texte.
	•	Kein Marketing-Buzzwording.
	•	Verwende Revolut/Finary UX als Referenz für Design-Beispiele.

⸻

(Dieses Dokument definiert den permanenten Kontext für alle Copilot-Vorschläge in FinGuide.
Copilot soll Code, Texte und Strukturen so gestalten, dass sie dieser Vision entsprechen.)

---

Möchtest du, dass ich dir zusätzlich ein **`docs/PRD.md`** erstelle, das Copilot und du gemeinsam pflegen könnt (also die inhaltliche Produktbeschreibung + Status-Tabelle + Roadmap)?  
Das wäre der logische nächste Schritt, um dein Projekt für Teamarbeit + AI-Assistenz perfekt vorzubereiten.