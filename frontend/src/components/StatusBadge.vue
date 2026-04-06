<template>
  <span :class="['badge', `badge-${status}`]">{{ label }}</span>
</template>

<script setup>
const props = defineProps({ status: String })
const labels = { draft: 'Borrador', published: 'Publicado', cancelled: 'Cancelado' }
const label = labels[props.status] ?? props.status
</script>
