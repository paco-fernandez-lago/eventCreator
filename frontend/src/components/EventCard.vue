<template>
  <div class="card event-card">
    <div class="event-card-header">
      <StatusBadge :status="event.status" />
      <span class="event-category">{{ event.category }}</span>
    </div>

    <h3 class="event-title">
      <router-link :to="`/events/${event.id}`">{{ event.title }}</router-link>
    </h3>

    <div class="event-meta">
      <span>📅 {{ formatDate(event.eventDate) }}</span>
      <span>🕐 {{ event.eventTime }}</span>
    </div>
    <div class="event-meta">
      <span>📍 {{ event.location }}</span>
    </div>
    <div class="event-meta" v-if="event.price > 0">
      <span>💶 {{ event.price.toFixed(2) }} €</span>
    </div>
    <div class="event-meta" v-else>
      <span>🎟 Entrada gratuita</span>
    </div>

    <div class="actions-row">
      <router-link :to="`/events/${event.id}`" class="btn btn-outline btn-sm">Ver</router-link>
      <router-link :to="`/events/${event.id}/edit`" class="btn btn-outline btn-sm">Editar</router-link>
      <button class="btn btn-danger btn-sm" @click="$emit('delete', event.id)">Eliminar</button>
    </div>
  </div>
</template>

<script setup>
import StatusBadge from './StatusBadge.vue'

defineProps({ event: { type: Object, required: true } })
defineEmits(['delete'])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const [y, m, d] = dateStr.split('-')
  return `${d}/${m}/${y}`
}
</script>

<style scoped>
.event-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: .5rem;
}
.event-category {
  font-size: .75rem;
  color: var(--color-muted);
  font-weight: 500;
}
.event-title {
  font-size: 1.05rem;
  font-weight: 600;
  margin-bottom: .5rem;
}
.event-title a:hover { color: var(--color-primary); }
.event-meta {
  font-size: .85rem;
  color: var(--color-muted);
  margin-bottom: .2rem;
  display: flex;
  gap: 1rem;
}
</style>
