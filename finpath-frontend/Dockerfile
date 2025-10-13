FROM node:22-alpine AS builder
WORKDIR /app

# 1) EXPLIZIT kopieren (anstatt Wildcard)
COPY package.json package-lock.json ./

# 2) Sicherstellen, dass sie wirklich da sind (früher Fehler statt späteres NPM-ENOENT)
RUN ls -la && test -f package.json && test -f package-lock.json

RUN npm ci

COPY . .
RUN npm run build -- --configuration=production

FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/dist/finpath-frontend/browser /usr/share/nginx/html
# falls du keine eigene nginx conf hast, diese Zeile weglassen:
# COPY nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
