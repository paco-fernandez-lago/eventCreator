# Event Creator

Web application for managing events built with Vue 3 + Kotlin/Ktor + PostgreSQL.

## Stack

| Layer | Technology |
|---|---|
| Frontend | Vue 3 + Vite + Vue Router |
| Backend | Kotlin + Ktor |
| Database | PostgreSQL 16 |
| ORM | Exposed |
| Containers | Docker + Docker Compose |

---

## Requirements

- Docker >= 24
- Docker Compose >= 2.20

For local development without Docker:
- JDK 21
- Gradle 8
- Node.js 20
- PostgreSQL 16

---

## Running with Docker (recommended)

```bash
# 1. Clone or unzip the project
cd eventCreator

# 2. Build images and start services
docker compose up --build

# 3. Open the app in your browser
open http://localhost:5173
```

All three services start automatically:
- **frontend** → http://localhost:5173
- **backend**  → http://localhost:8080
- **postgres** → localhost:5432

Migrations and seed data run automatically when the backend starts.

To stop everything:
```bash
docker compose down
```

To also remove the database volume:
```bash
docker compose down -v
```

---

## Local development (without Docker)

### Database

```bash
# Run only Postgres in Docker
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

# Copy environment config
cp .env.example .env

# Start the server (migrations run automatically on startup)
./gradlew run
# Available at http://localhost:8080
```

### Run backend tests

```bash
cd backend
./gradlew test
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# Available at http://localhost:5173
```

---

## Project structure

```
eventCreator/
├── docker-compose.yml
├── api-examples.http          ← HTTP request examples
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

## REST API

Base URL: `http://localhost:8080/api`

| Method | Route | Description |
|---|---|---|
| GET | `/events` | List events. Params: `search`, `category`, `status`, `sort` |
| GET | `/events/{id}` | Get event detail |
| POST | `/events` | Create event |
| PUT | `/events/{id}` | Update event |
| DELETE | `/events/{id}` | Delete event |

See `api-examples.http` for full examples of all requests.

### Example: create an event

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Event",
    "eventDate": "2026-06-01",
    "eventTime": "10:00",
    "location": "Madrid",
    "category": "Technology",
    "capacity": 100,
    "price": 0,
    "organizer": "Me",
    "status": "draft"
  }'
```

---

## Data model

Table `events`:

| Field | Type | Constraints |
|---|---|---|
| id | UUID | PK, auto-generated |
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

## Backend environment variables

See `backend/.env.example`:

| Variable | Default | Description |
|---|---|---|
| PORT | 8080 | Server port |
| DB_URL | jdbc:postgresql://localhost:5432/eventcreator | Connection URL |
| DB_USER | eventuser | PostgreSQL user |
| DB_PASSWORD | eventpass | PostgreSQL password |
