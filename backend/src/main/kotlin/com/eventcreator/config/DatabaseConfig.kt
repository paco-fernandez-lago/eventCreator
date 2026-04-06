package com.eventcreator.config

import com.eventcreator.models.Events
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun initDatabase(url: String, user: String, password: String) {
    val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl         = url
        username        = user
        this.password   = password
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
    })

    Database.connect(dataSource)

    transaction {
        // Crear la tabla si no existe, preservando todas las constraints
        exec("""
            CREATE TABLE IF NOT EXISTS events (
                id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                title       VARCHAR(255)    NOT NULL,
                description TEXT,
                event_date  DATE            NOT NULL,
                event_time  TIME            NOT NULL,
                location    VARCHAR(255)    NOT NULL,
                category    VARCHAR(100)    NOT NULL,
                capacity    INTEGER         NOT NULL CONSTRAINT capacity_non_negative CHECK (capacity >= 0),
                price       NUMERIC(10, 2)  NOT NULL DEFAULT 0 CONSTRAINT price_non_negative CHECK (price >= 0),
                organizer   VARCHAR(255)    NOT NULL,
                status      VARCHAR(20)     NOT NULL DEFAULT 'draft'
                                            CONSTRAINT valid_status CHECK (status IN ('draft', 'published', 'cancelled')),
                created_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
                updated_at  TIMESTAMPTZ     NOT NULL DEFAULT now()
            )
        """)

        exec("CREATE INDEX IF NOT EXISTS idx_events_status   ON events(status)")
        exec("CREATE INDEX IF NOT EXISTS idx_events_category ON events(category)")
        exec("CREATE INDEX IF NOT EXISTS idx_events_date     ON events(event_date)")

        // Seed solo si la tabla está vacía
        val count = Events.selectAll().count()
        if (count == 0L) {
            exec("""
                INSERT INTO events (title, description, event_date, event_time, location, category, capacity, price, organizer, status)
                VALUES
                ('Conferencia de Tecnología 2026',
                 'Un evento anual que reúne a profesionales del sector tech para compartir conocimientos sobre IA, cloud y desarrollo de software.',
                 '2026-05-15', '09:00:00', 'Palacio de Congresos, Madrid', 'Tecnología', 500, 49.99, 'TechEvents Spain', 'published'),
                ('Taller de Vue 3 Avanzado',
                 'Workshop práctico de 4 horas sobre composables, Pinia y optimización en Vue 3. Plazas muy limitadas.',
                 '2026-06-03', '10:00:00', 'Coworking Hub, Barcelona', 'Taller', 30, 25.00, 'Vue Barcelona Meetup', 'published'),
                ('Festival de Música Indie',
                 'Dos días de música en directo con más de 20 artistas independientes de toda España.',
                 '2026-07-12', '17:00:00', 'Parque del Retiro, Madrid', 'Música', 2000, 15.00, 'IndieSound Producciones', 'draft'),
                ('Maratón Solidaria Ciudad de Sevilla',
                 'Carrera popular de 42 km con fines benéficos.',
                 '2026-04-20', '08:30:00', 'Plaza de España, Sevilla', 'Deporte', 1500, 10.00, 'Atletismo Sevilla', 'published'),
                ('Exposición Arte Digital Emergente',
                 'Muestra de obras generadas con inteligencia artificial y arte digital de artistas emergentes europeos.',
                 '2026-08-01', '11:00:00', 'Centro de Arte Contemporáneo, Valencia', 'Arte', 300, 0.00, 'CAC Valencia', 'cancelled')
            """)
        }
    }
}
