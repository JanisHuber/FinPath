Infra per Compose 
===================
cd ops
docker compose -f compose.dev.yml up -d db

# Run the backend
DB_HOST=localhost DB_PORT=5432 DB_NAME=finguide DB_USER=finguide DB_PASS=secret \
mvn liberty:

# Run the frontend
cd ../frontend

npm install

npm start