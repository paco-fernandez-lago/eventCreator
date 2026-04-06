# Event Creator — Guía para Claude

## Qué es este proyecto

Aplicación web full-stack para gestionar eventos. MVP sin autenticación, pensado para ser simple y extensible.

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | Vue 3 + Vite + Vue Router + Axios |
| Backend | Kotlin + Ktor 2.3.x |
| ORM | Exposed 0.53 |
| Base de datos | PostgreSQL 16 |
| Contenedores | Docker + Docker Compose |

> Flyway fue descartado — el classpath scanner no funciona correctamente en fat JARs. Las migraciones y el seed se ejecutan directamente en `DatabaseConfig.kt` via Exposed `transaction { exec(...) }`.

## Arrancar el proyecto

```bash
docker compose up --build
# Frontend → http://localhost:5173
# Backend  → http://localhost:8080/api/events
```

## Estructura

```
eventCreator/
├── docker-compose.yml
├── api-examples.http          ← ejemplos de peticiones (IntelliJ / VS Code REST Client)
│
├── backend/
│   ├── build.gradle.kts       ← dependencias Gradle
│   ├── .env.example           ← variables de entorno necesarias
│   └── src/main/kotlin/com/eventcreator/
│       ├── Application.kt         ← entry point: JSON, CORS, StatusPages, init DB, rutas
│       ├── config/
│       │   └── DatabaseConfig.kt  ← HikariCP pool + CREATE TABLE IF NOT EXISTS + seed
│       ├── models/
│       │   └── Event.kt           ← Exposed Table object
│       ├── dto/
│       │   └── EventDto.kt        ← EventRequest, EventResponse, ErrorResponse, validate()
│       ├── repositories/
│       │   └── EventRepository.kt ← CRUD con filtros via Exposed DSL
│       ├── services/
│       │   └── EventService.kt    ← validación, ValidationException, NotFoundException
│       └── routes/
│           └── EventRoutes.kt     ← 5 endpoints REST
│
└── frontend/src/
    ├── api/events.js          ← cliente Axios centralizado (VITE_API_URL)
    ├── router/index.js        ← 4 rutas con history mode
    ├── components/
    │   ├── EventForm.vue      ← formulario compartido crear/editar (props: initialData, loading)
    │   ├── EventCard.vue      ← tarjeta con acciones
    │   ├── FilterBar.vue      ← búsqueda + filtros (v-model)
    │   ├── ConfirmDialog.vue  ← modal de confirmación
    │   └── StatusBadge.vue    ← badge por estado (draft/published/cancelled)
    └── views/
        ├── EventList.vue      ← listado con filtrado client-side
        ├── EventDetail.vue    ← detalle + borrado
        ├── EventCreate.vue    ← wraps EventForm
        └── EventEdit.vue      ← wraps EventForm con carga previa
```

## API REST

Base URL: `http://localhost:8080/api`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/events` | Lista. Params: `search`, `category`, `status`, `sort` |
| GET | `/events/{id}` | Detalle |
| POST | `/events` | Crear → 201 |
| PUT | `/events/{id}` | Actualizar → 200 |
| DELETE | `/events/{id}` | Eliminar → 204 |

Errores: `{ "error": "mensaje" }` con código HTTP apropiado.

## Modelo de datos (`events`)

| Campo | Tipo | Restricciones |
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

## Decisiones técnicas relevantes

- **Sin Flyway**: las migraciones usan `exec("CREATE TABLE IF NOT EXISTS ...")` directamente en `DatabaseConfig.kt`. El seed se inserta solo si la tabla está vacía.
- **Sin autenticación**: decisión explícita del MVP.
- **Filtrado client-side**: `EventList.vue` filtra localmente para evitar round-trips innecesarios.
- **CSS propio**: sin librerías UI externas, todo en `src/assets/main.css` con variables CSS.
- **`Events` extiende `Table`** (no `IdTable`): usar `insert { }[Events.id]` para obtener el UUID insertado, no `insertAndGetId`.

## Tests

```bash
cd backend && ./gradlew test
```

Tests en `EventRoutesTest.kt` con H2 en memoria. Cubren: GET lista, POST válido, POST inválido, GET 404, DELETE.

## Variables de entorno del backend

Ver `backend/.env.example`:

```
PORT=8080
DB_URL=jdbc:postgresql://localhost:5432/eventcreator
DB_USER=eventuser
DB_PASSWORD=eventpass
```
