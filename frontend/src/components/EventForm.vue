<template>
  <form @submit.prevent="handleSubmit" novalidate>
    <div class="form-row">
      <div class="form-group">
        <label class="form-label">Título *</label>
        <input v-model="form.title" class="form-control" :class="{ 'form-error-border': errors.title }" :disabled="loading" />
        <span v-if="errors.title" class="form-error">{{ errors.title }}</span>
      </div>
      <div class="form-group">
        <label class="form-label">Organizador *</label>
        <input v-model="form.organizer" class="form-control" :class="{ 'form-error-border': errors.organizer }" :disabled="loading" />
        <span v-if="errors.organizer" class="form-error">{{ errors.organizer }}</span>
      </div>
    </div>

    <div class="form-group">
      <label class="form-label">Descripción</label>
      <textarea v-model="form.description" class="form-control" :disabled="loading" rows="3" />
    </div>

    <div class="form-row">
      <div class="form-group">
        <label class="form-label">Fecha *</label>
        <input type="date" v-model="form.eventDate" class="form-control" :class="{ 'form-error-border': errors.eventDate }" :disabled="loading" />
        <span v-if="errors.eventDate" class="form-error">{{ errors.eventDate }}</span>
      </div>
      <div class="form-group">
        <label class="form-label">Hora *</label>
        <input type="time" v-model="form.eventTime" class="form-control" :class="{ 'form-error-border': errors.eventTime }" :disabled="loading" />
        <span v-if="errors.eventTime" class="form-error">{{ errors.eventTime }}</span>
      </div>
    </div>

    <div class="form-row">
      <div class="form-group">
        <label class="form-label">Ubicación *</label>
        <input v-model="form.location" class="form-control" :class="{ 'form-error-border': errors.location }" :disabled="loading" />
        <span v-if="errors.location" class="form-error">{{ errors.location }}</span>
      </div>
      <div class="form-group">
        <label class="form-label">Categoría *</label>
        <input v-model="form.category" class="form-control" list="category-suggestions" :class="{ 'form-error-border': errors.category }" :disabled="loading" />
        <datalist id="category-suggestions">
          <option v-for="c in CATEGORIES" :key="c" :value="c" />
        </datalist>
        <span v-if="errors.category" class="form-error">{{ errors.category }}</span>
      </div>
    </div>

    <div class="form-row">
      <div class="form-group">
        <label class="form-label">Capacidad máxima *</label>
        <input type="number" min="0" v-model.number="form.capacity" class="form-control" :class="{ 'form-error-border': errors.capacity }" :disabled="loading" />
        <span v-if="errors.capacity" class="form-error">{{ errors.capacity }}</span>
      </div>
      <div class="form-group">
        <label class="form-label">Precio (€)</label>
        <input type="number" min="0" step="0.01" v-model.number="form.price" class="form-control" :class="{ 'form-error-border': errors.price }" :disabled="loading" />
        <span v-if="errors.price" class="form-error">{{ errors.price }}</span>
      </div>
    </div>

    <div class="form-group">
      <label class="form-label">Estado</label>
      <select v-model="form.status" class="form-control" :disabled="loading">
        <option value="draft">Borrador</option>
        <option value="published">Publicado</option>
        <option value="cancelled">Cancelado</option>
      </select>
    </div>

    <div class="actions-row" style="margin-top:1.5rem">
      <button type="submit" class="btn btn-primary" :disabled="loading">
        <span v-if="loading" class="spinner" style="width:14px;height:14px;border-width:2px" />
        {{ loading ? 'Guardando...' : submitLabel }}
      </button>
      <button type="button" class="btn btn-outline" @click="$emit('cancel')" :disabled="loading">
        Cancelar
      </button>
    </div>
  </form>
</template>

<script setup>
import { reactive, watch } from 'vue'

const CATEGORIES = ['Tecnología', 'Música', 'Arte', 'Deporte', 'Taller', 'Conferencia', 'Gastronomía', 'Otro']

const props = defineProps({
  initialData: { type: Object, default: null },
  loading:     { type: Boolean, default: false },
  submitLabel: { type: String, default: 'Guardar' }
})
const emit = defineEmits(['submit', 'cancel'])

const emptyForm = () => ({
  title: '', description: '', eventDate: '', eventTime: '',
  location: '', category: '', capacity: 0, price: 0,
  organizer: '', status: 'draft'
})

const form   = reactive(emptyForm())
const errors = reactive({})

watch(() => props.initialData, (data) => {
  if (data) Object.assign(form, data)
}, { immediate: true })

function validate() {
  Object.keys(errors).forEach(k => delete errors[k])
  if (!form.title.trim())     errors.title     = 'El título es obligatorio'
  if (!form.organizer.trim()) errors.organizer = 'El organizador es obligatorio'
  if (!form.location.trim())  errors.location  = 'La ubicación es obligatoria'
  if (!form.category.trim())  errors.category  = 'La categoría es obligatoria'
  if (!form.eventDate)        errors.eventDate = 'La fecha es obligatoria'
  if (!form.eventTime)        errors.eventTime = 'La hora es obligatoria'
  if (form.capacity < 0)      errors.capacity  = 'No puede ser negativa'
  if (form.price < 0)         errors.price     = 'No puede ser negativo'
  return Object.keys(errors).length === 0
}

function handleSubmit() {
  if (!validate()) return
  emit('submit', { ...form })
}
</script>

<style scoped>
.form-error-border { border-color: var(--color-danger) !important; }
</style>
