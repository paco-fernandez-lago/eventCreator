<template>
  <div class="modal-overlay" @click.self="$emit('cancel')">
    <div class="modal">
      <h3 class="modal-title">{{ title }}</h3>
      <p class="modal-body">{{ message }}</p>
      <div class="modal-actions">
        <button class="btn btn-outline" @click="$emit('cancel')">Cancelar</button>
        <button class="btn btn-danger" @click="$emit('confirm')">Confirmar</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title:   { type: String, default: '¿Estás seguro?' },
  message: { type: String, default: 'Esta acción no se puede deshacer.' }
})
defineEmits(['confirm', 'cancel'])
</script>
