Infra per Compose
===================
cd ops
docker compose -f compose.dev.yml up -d db

# Run the backend
DB_HOST=localhost DB_PORT=5432 DB_NAME=finguide DB_USER=finguide DB_PASS=secret \
mvn liberty:dev

# Development: web frontend
cd apps/finpath-frontend-web

npm install

npm start

# Development: Mobile
cd apps/finpath-frontend-mobile
## for iOS simulator
npm run dev:ios
## for Android emulator
npm run dev:android
## to open in Xcode or Android Studio
npx cap open ios | android