<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Nuevo evento</h1>
    </div>

    <div v-if="error" class="alert alert-error">{{ error }}</div>

    <div class="card">
      <EventForm
        submit-label="Crear evento"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="$router.push('/')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import EventForm from '../components/EventForm.vue'
import { createEvent } from '../api/events.js'

const router  = useRouter()
const loading = ref(false)
const error   = ref(null)

async function handleSubmit(data) {
  loading.value = true
  error.value   = null
  try {
    const created = await createEvent(data)
    router.push(`/events/${created.id}`)
  } catch (e) {
    error.value = e.response?.data?.error ?? 'Error al crear el evento.'
    loading.value = false
  }
}
</script>
