<template>
  <div class="filter-bar">
    <div class="form-group" style="flex:1; min-width:200px; margin:0">
      <input
        class="form-control"
        type="text"
        placeholder="Buscar por título..."
        :value="modelValue.search"
        @input="update('search', $event.target.value)"
      />
    </div>

    <select class="form-control" :value="modelValue.category" @change="update('category', $event.target.value)">
      <option value="">Todas las categorías</option>
      <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
    </select>

    <select class="form-control" :value="modelValue.status" @change="update('status', $event.target.value)">
      <option value="">Todos los estados</option>
      <option value="draft">Borrador</option>
      <option value="published">Publicado</option>
      <option value="cancelled">Cancelado</option>
    </select>

    <select class="form-control" :value="modelValue.sort" @change="update('sort', $event.target.value)">
      <option value="">Más recientes</option>
      <option value="date_asc">Fecha: más próximos</option>
      <option value="date_desc">Fecha: más lejanos</option>
    </select>
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: { type: Object, required: true },
  categories: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:modelValue'])

function update(key, value) {
  emit('update:modelValue', { ...props.modelValue, [key]: value })
}
</script>
