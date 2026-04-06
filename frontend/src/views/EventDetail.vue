<template>
  <div>
    <div v-if="loading" class="loading-center"><span class="spinner" /></div>

    <div v-else-if="error" class="alert alert-error">{{ error }}</div>

    <template v-else-if="event">
      <div class="page-header">
        <h1 class="page-title">{{ event.title }}</h1>
        <div style="display:flex; gap:.5rem">
          <router-link :to="`/events/${event.id}/edit`" class="btn btn-outline">Editar</router-link>
          <button class="btn btn-danger" @click="confirmDelete = true">Eliminar</button>
        </div>
      </div>

      <div class="card" style="margin-bottom:1.5rem">
        <div style="display:flex; align-items:center; gap:.75rem; margin-bottom:1.25rem">
          <StatusBadge :status="event.status" />
          <span style="color:var(--color-muted); font-size:.875rem">{{ event.category }}</span>
        </div>

        <p v-if="event.description" style="margin-bottom:1.5rem; line-height:1.7">{{ event.description }}</p>

        <div class="detail-grid">
          <div class="detail-field">
            <span class="detail-label">Fecha</span>
            <span class="detail-value">📅 {{ formatDate(event.eventDate) }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Hora</span>
            <span class="detail-value">🕐 {{ event.eventTime }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Ubicación</span>
            <span class="detail-value">📍 {{ event.location }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Organizador</span>
            <span class="detail-value">👤 {{ event.organizer }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Capacidad</span>
            <span class="detail-value">👥 {{ event.capacity }} personas</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Precio</span>
            <span class="detail-value">
              {{ event.price > 0 ? `💶 ${Number(event.price).toFixed(2)} €` : '🎟 Gratuito' }}
            </span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Creado</span>
            <span class="detail-value">{{ formatDatetime(event.createdAt) }}</span>
          </div>
          <div class="detail-field">
            <span class="detail-label">Última modificación</span>
            <span class="detail-value">{{ formatDatetime(event.updatedAt) }}</span>
          </div>
        </div>
      </div>

      <router-link to="/" class="btn btn-outline">← Volver al listado</router-link>
    </template>

    <ConfirmDialog
      v-if="confirmDelete"
      title="Eliminar evento"
      message="¿Seguro que quieres eliminar este evento? Esta acción no se puede deshacer."
      @confirm="handleDelete"
      @cancel="confirmDelete = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StatusBadge   from '../components/StatusBadge.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import { getEvent, deleteEvent } from '../api/events.js'

const route  = useRoute()
const router = useRouter()

const event         = ref(null)
const loading       = ref(false)
const error         = ref(null)
const confirmDelete = ref(false)

async function load() {
  loading.value = true
  error.value   = null
  try {
    event.value = await getEvent(route.params.id)
  } catch (e) {
    error.value = e.response?.status === 404
      ? 'Evento no encontrado.'
      : 'Error al cargar el evento.'
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  confirmDelete.value = false
  try {
    await deleteEvent(event.value.id)
    router.push('/')
  } catch {
    error.value = 'No se pudo eliminar el evento.'
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [y, m, d] = dateStr.split('-')
  return `${d}/${m}/${y}`
}

function formatDatetime(str) {
  if (!str) return ''
  return new Date(str).toLocaleString('es-ES', { dateStyle: 'medium', timeStyle: 'short' })
}

onMounted(load)
</script>
