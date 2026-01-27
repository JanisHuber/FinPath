## Deployment local:
### Frontend:
```bash
bun run -c local
```
### Backend:
```bash
cd ..
cd apps/finpath-backend/
SPRING_PROFILES_ACTIVE=loc JDBC_URL="jdbc:postgresql://..." DB_USER="..." DB_PASS="..." mvn spring-boot:run
```


## Deployment dev:
via github actions pipeline [branch: dev]

## Deployment prd:
via github actions pipeline PR dev -> prod
