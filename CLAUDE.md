# Event Creator — Guide for Claude

## What this project is

Full-stack web application for managing events. MVP with no authentication, designed to be simple and extensible.

## Stack

| Layer | Technology |
|---|---|
| Frontend | Vue 3 + Vite + Vue Router + Axios |
| Backend | Kotlin + Ktor 2.3.x |
| ORM | Exposed 0.53 |
| Database | PostgreSQL 16 |
| Containers | Docker + Docker Compose |

> Flyway was dropped — the classpath scanner does not work correctly inside fat JARs. Migrations and seed data are executed directly in `DatabaseConfig.kt` via Exposed `transaction { exec(...) }`.

## Running the project

```bash
docker compose up --build
# Frontend → http://localhost:5173
# Backend  → http://localhost:8080/api/events
```

## Structure

```
eventCreator/
├── docker-compose.yml
├── api-examples.http          ← request examples (IntelliJ / VS Code REST Client)
│
├── backend/
│   ├── build.gradle.kts       ← Gradle dependencies
│   ├── .env.example           ← required environment variables
│   └── src/main/kotlin/com/eventcreator/
│       ├── Application.kt         ← entry point: JSON, CORS, StatusPages, DB init, routes
│       ├── config/
│       │   └── DatabaseConfig.kt  ← HikariCP pool + CREATE TABLE IF NOT EXISTS + seed
│       ├── models/
│       │   └── Event.kt           ← Exposed Table object
│       ├── dto/
│       │   └── EventDto.kt        ← EventRequest, EventResponse, ErrorResponse, validate()
│       ├── repositories/
│       │   └── EventRepository.kt ← CRUD with filters via Exposed DSL
│       ├── services/
│       │   └── EventService.kt    ← validation, ValidationException, NotFoundException
│       └── routes/
│           └── EventRoutes.kt     ← 5 REST endpoints
│
└── frontend/src/
    ├── api/events.js          ← centralised Axios client (VITE_API_URL)
    ├── router/index.js        ← 4 routes with history mode
    ├── components/
    │   ├── EventForm.vue      ← shared create/edit form (props: initialData, loading)
    │   ├── EventCard.vue      ← card with actions
    │   ├── FilterBar.vue      ← search + filters (v-model)
    │   ├── ConfirmDialog.vue  ← confirmation modal
    │   └── StatusBadge.vue    ← badge by status (draft/published/cancelled)
    └── views/
        ├── EventList.vue      ← listing with client-side filtering
        ├── EventDetail.vue    ← full detail + delete
        ├── EventCreate.vue    ← wraps EventForm
        └── EventEdit.vue      ← wraps EventForm with pre-loaded data
```

## REST API

Base URL: `http://localhost:8080/api`

| Method | Route | Description |
|---|---|---|
| GET | `/events` | List. Params: `search`, `category`, `status`, `sort` |
| GET | `/events/{id}` | Detail |
| POST | `/events` | Create → 201 |
| PUT | `/events/{id}` | Update → 200 |
| DELETE | `/events/{id}` | Delete → 204 |

Errors: `{ "error": "message" }` with appropriate HTTP status code.

## Data model (`events`)

| Field | Type | Constraints |
|---|---|---|
| id | UUID | PK, `gen_random_uuid()` |
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

## Key technical decisions

- **No Flyway**: migrations use `exec("CREATE TABLE IF NOT EXISTS ...")` directly in `DatabaseConfig.kt`. Seed data is inserted only if the table is empty.
- **No authentication**: explicit decision for the MVP.
- **Client-side filtering**: `EventList.vue` filters locally to avoid unnecessary round-trips.
- **Custom CSS**: no external UI libraries; everything is in `src/assets/main.css` using CSS variables.
- **`Events` extends `Table`** (not `IdTable`): use `insert { }[Events.id]` to get the inserted UUID, not `insertAndGetId`.

## Tests

```bash
cd backend && ./gradlew test
```

Tests in `EventRoutesTest.kt` using an in-memory H2 database. Cover: GET list, valid POST, invalid POST, GET 404, DELETE.

## Backend environment variables

See `backend/.env.example`:

```
PORT=8080
DB_URL=jdbc:postgresql://localhost:5432/eventcreator
DB_USER=eventuser
DB_PASSWORD=eventpass
```
