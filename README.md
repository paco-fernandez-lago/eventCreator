# Event Creator

Aplicación web para gestionar eventos con Vue 3 + Kotlin/Ktor + PostgreSQL.

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | Vue 3 + Vite + Vue Router |
| Backend | Kotlin + Ktor |
| Base de datos | PostgreSQL 16 |
| ORM | Exposed |
| Migraciones | Flyway |
| Contenedores | Docker + Docker Compose |

---

## Requisitos

- Docker >= 24
- Docker Compose >= 2.20

Para desarrollo local sin Docker:
- JDK 21
- Gradle 8
- Node.js 20
- PostgreSQL 16

---

## Arrancar con Docker (recomendado)

```bash
# 1. Clonar o descomprimir el proyecto
cd eventCreator

# 2. Construir imágenes y levantar servicios
docker compose up --build

# 3. Abrir la aplicación en el navegador
open http://localhost:5173
```

Los tres servicios se arrancan automáticamente:
- **frontend** → http://localhost:5173
- **backend**  → http://localhost:8080
- **postgres** → localhost:5432

Las migraciones y el seed de datos de ejemplo se ejecutan automáticamente al arrancar el backend.

Para detener todo:
```bash
docker compose down
```

Para eliminar también los datos de la base de datos:
```bash
docker compose down -v
```

---

## Desarrollo local (sin Docker)

### Base de datos

```bash
# Con Docker solo para Postgres
docker run -d \
  --name eventcreator-db \
  -e POSTGRES_DB=eventcreator \
  -e POSTGRES_USER=eventuser \
  -e POSTGRES_PASSWORD=eventpass \
  -p 5432:5432 \
  postgres:16
```

### Backend

```bash
cd backend

# Copiar configuración de entorno
cp .env.example .env

# Arrancar el servidor (Flyway ejecutará las migraciones automáticamente)
./gradlew run
# Disponible en http://localhost:8080
```

### Ejecutar tests del backend

```bash
cd backend
./gradlew test
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# Disponible en http://localhost:5173
```

---

## Estructura del proyecto

```
eventCreator/
├── docker-compose.yml
├── api-examples.http          ← ejemplos de peticiones HTTP
│
├── backend/
│   ├── Dockerfile
│   ├── build.gradle.kts
│   ├── .env.example
│   └── src/
│       ├── main/kotlin/com/eventcreator/
│       │   ├── Application.kt
│       │   ├── config/DatabaseConfig.kt
│       │   ├── models/Event.kt
│       │   ├── dto/EventDto.kt
│       │   ├── repositories/EventRepository.kt
│       │   ├── services/EventService.kt
│       │   └── routes/EventRoutes.kt
│       └── main/resources/
│           ├── application.conf
│           └── db/migration/
│               ├── V1__create_events_table.sql
│               └── V2__seed_events.sql
│
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    ├── src/
    │   ├── api/events.js
    │   ├── router/index.js
    │   ├── components/
    │   │   ├── EventCard.vue
    │   │   ├── EventForm.vue
    │   │   ├── FilterBar.vue
    │   │   ├── ConfirmDialog.vue
    │   │   └── StatusBadge.vue
    │   └── views/
    │       ├── EventList.vue
    │       ├── EventDetail.vue
    │       ├── EventCreate.vue
    │       └── EventEdit.vue
```

---

## API REST

Base URL: `http://localhost:8080/api`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/events` | Lista eventos. Params: `search`, `category`, `status`, `sort` |
| GET | `/events/{id}` | Detalle de un evento |
| POST | `/events` | Crear evento |
| PUT | `/events/{id}` | Actualizar evento |
| DELETE | `/events/{id}` | Eliminar evento |

Ver `api-examples.http` para ejemplos completos de todas las peticiones.

### Ejemplo: crear un evento

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Mi Evento",
    "eventDate": "2026-06-01",
    "eventTime": "10:00",
    "location": "Madrid",
    "category": "Tecnología",
    "capacity": 100,
    "price": 0,
    "organizer": "Yo",
    "status": "draft"
  }'
```

---

## Modelo de datos

Tabla `events`:

| Campo | Tipo | Restricciones |
|---|---|---|
| id | UUID | PK, autogenerado |
| title | VARCHAR(255) | NOT NULL |
| description | TEXT | nullable |
| event_date | DATE | NOT NULL |
| event_time | TIME | NOT NULL |
| location | VARCHAR(255) | NOT NULL |
| category | VARCHAR(100) | NOT NULL |
| capacity | INTEGER | NOT NULL, >= 0 |
| price | NUMERIC(10,2) | NOT NULL, >= 0 |
| organizer | VARCHAR(255) | NOT NULL |
| status | VARCHAR(20) | draft \| published \| cancelled |
| created_at | TIMESTAMPTZ | NOT NULL |
| updated_at | TIMESTAMPTZ | NOT NULL |

---

## Variables de entorno del backend

Ver `backend/.env.example`:

| Variable | Default | Descripción |
|---|---|---|
| PORT | 8080 | Puerto del servidor |
| DB_URL | jdbc:postgresql://localhost:5432/eventcreator | URL de conexión |
| DB_USER | eventuser | Usuario de PostgreSQL |
| DB_PASSWORD | eventpass | Contraseña de PostgreSQL |
