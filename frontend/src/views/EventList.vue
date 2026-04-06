<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Eventos</h1>
      <router-link to="/events/new" class="btn btn-primary">+ Nuevo evento</router-link>
    </div>

    <div v-if="alert.message" :class="['alert', `alert-${alert.type}`]">{{ alert.message }}</div>

    <FilterBar v-model="filters" :categories="uniqueCategories" />

    <div v-if="loading" class="loading-center"><span class="spinner" /></div>

    <div v-else-if="error" class="alert alert-error">{{ error }}</div>

    <template v-else>
      <p v-if="filtered.length === 0" class="empty-state">
        <strong>Sin resultados</strong>
        <p>No hay eventos que coincidan con los filtros aplicados.</p>
      </p>
      <div v-else class="events-grid">
        <EventCard
          v-for="event in filtered"
          :key="event.id"
          :event="event"
          @delete="confirmDelete"
        />
      </div>
    </template>

    <ConfirmDialog
      v-if="deleteTarget"
      title="Eliminar evento"
      message="¿Seguro que quieres eliminar este evento? Esta acción no se puede deshacer."
      @confirm="handleDelete"
      @cancel="deleteTarget = null"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import EventCard     from '../components/EventCard.vue'
import FilterBar     from '../components/FilterBar.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import { getEvents, deleteEvent } from '../api/events.js'

const events       = ref([])
const loading      = ref(false)
const error        = ref(null)
const deleteTarget = ref(null)
const alert        = reactive({ message: '', type: 'success' })

const filters = reactive({ search: '', category: '', status: '', sort: '' })

const uniqueCategories = computed(() => [...new Set(events.value.map(e => e.category))].sort())

const filtered = computed(() => {
  let list = events.value
  if (filters.search)   list = list.filter(e => e.title.toLowerCase().includes(filters.search.toLowerCase()))
  if (filters.category) list = list.filter(e => e.category === filters.category)
  if (filters.status)   list = list.filter(e => e.status === filters.status)
  if (filters.sort === 'date_asc')  list = [...list].sort((a, b) => a.eventDate.localeCompare(b.eventDate))
  if (filters.sort === 'date_desc') list = [...list].sort((a, b) => b.eventDate.localeCompare(a.eventDate))
  return list
})

async function load() {
  loading.value = true
  error.value   = null
  try {
    events.value = await getEvents()
  } catch {
    error.value = 'No se pudieron cargar los eventos. Comprueba que el backend está en marcha.'
  } finally {
    loading.value = false
  }
}

function confirmDelete(id) { deleteTarget.value = id }

async function handleDelete() {
  const id = deleteTarget.value
  deleteTarget.value = null
  try {
    await deleteEvent(id)
    events.value = events.value.filter(e => e.id !== id)
    showAlert('Evento eliminado correctamente.', 'success')
  } catch {
    showAlert('No se pudo eliminar el evento.', 'error')
  }
}

function showAlert(message, type) {
  alert.message = message
  alert.type    = type
  setTimeout(() => { alert.message = '' }, 3000)
}

onMounted(load)
</script>
