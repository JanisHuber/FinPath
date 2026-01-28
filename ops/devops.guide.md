## Deployment local:
### Frontend:
```bash
bun run -c local
```
### Backend:
```bash
cd apps/finpath-backend/
SPRING_PROFILES_ACTIVE=loc mvn spring-boot:run
```
### Database:
```bash
supabase start
```

## Deployment dev:
via github actions pipeline [branch: dev]

## Deployment prd:
via github actions pipeline PR dev -> prod


## Database

### Environments:
| Environment | Type | Connection |
|-------------|------|------------|
| local | Supabase CLI | localhost:54322 |
| dev | Supabase Cloud | db.xxx.supabase.co |
| prd | Supabase Cloud | db.yyy.supabase.co |

### Local Commands:
```bash
supabase start              # Startet lokalen Stack
supabase stop               # Stoppt lokalen Stack
supabase db reset           # Reset + alle Migrations
supabase db diff -f <name>  # Neue Migration generieren
supabase status             # Zeigt URLs und Keys
```

### Dashboard:
http://127.0.0.1:54323

### Migration Workflow:

1. Lokal entwickeln:
```bash
# Schema ändern im Dashboard oder SQL
supabase db diff -f add_new_feature
# -> erstellt supabase/migrations/TIMESTAMP_add_new_feature.sql
```

2. Testen:
```bash
supabase db reset  # Reset und alle Migrations anwenden
```

3. Commit & Push:
```bash
git add supabase/migrations/
git commit -m "Add new feature migration"
git push origin dev
```

4. CI/CD macht den Rest:
- Push to `dev` -> Migrations auf dev-Supabase
- PR dev -> prod -> Migrations auf prd-Supabase


### GitHub Secrets (Required):
```
SUPABASE_ACCESS_TOKEN      # Personal Access Token von supabase.com/dashboard/account/tokens
SUPABASE_PROJECT_REF_DEV   # Project Reference von dev (z.B. odbjralfrvmgjpcdhziu)
SUPABASE_PROJECT_REF_PRD   # Project Reference von prod
SUPABASE_PASS_DEV          # Database Password dev
SUPABASE_PASS_PRD          # Database Password prod
```

### Migrations Struktur:
```
supabase/
├── config.toml
├── seed.sql                              # Test-Daten (nur local)
└── migrations/
    ├── 20260128083256_create_profiles.sql
    └── 20260129000000_add_accounts.sql   # Neue Migration
```

### Wichtig:
- Migrations nie editieren nachdem sie gepusht wurden
- Immer neue Migration erstellen für Änderungen
- `supabase db reset` lokal testen vor Push
