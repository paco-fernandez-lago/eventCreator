<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Editar evento</h1>
    </div>

    <div v-if="loadError" class="alert alert-error">{{ loadError }}</div>
    <div v-if="saveError" class="alert alert-error">{{ saveError }}</div>

    <div v-if="fetching" class="loading-center"><span class="spinner" /></div>

    <div v-else-if="initialData" class="card">
      <EventForm
        submit-label="Guardar cambios"
        :initial-data="initialData"
        :loading="saving"
        @submit="handleSubmit"
        @cancel="$router.push(`/events/${route.params.id}`)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EventForm from '../components/EventForm.vue'
import { getEvent, updateEvent } from '../api/events.js'

const route  = useRoute()
const router = useRouter()

const initialData = ref(null)
const fetching    = ref(false)
const saving      = ref(false)
const loadError   = ref(null)
const saveError   = ref(null)

async function load() {
  fetching.value = true
  loadError.value = null
  try {
    const event = await getEvent(route.params.id)
    // Normalise eventTime to "HH:mm" for the input[type=time]
    initialData.value = { ...event, eventTime: event.eventTime.substring(0, 5) }
  } catch (e) {
    loadError.value = e.response?.status === 404
      ? 'Evento no encontrado.'
      : 'Error al cargar el evento.'
  } finally {
    fetching.value = false
  }
}

async function handleSubmit(data) {
  saving.value    = true
  saveError.value = null
  try {
    await updateEvent(route.params.id, data)
    router.push(`/events/${route.params.id}`)
  } catch (e) {
    saveError.value = e.response?.data?.error ?? 'Error al guardar el evento.'
    saving.value = false
  }
}

onMounted(load)
</script>
