import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api` : '/api'
})

export function getEvents(filters = {}) {
  return api.get('/events', { params: filters }).then(r => r.data)
}

export function getEvent(id) {
  return api.get(`/events/${id}`).then(r => r.data)
}

export function createEvent(data) {
  return api.post('/events', data).then(r => r.data)
}

export function updateEvent(id, data) {
  return api.put(`/events/${id}`, data).then(r => r.data)
}

export function deleteEvent(id) {
  return api.delete(`/events/${id}`)
}
