import { createRouter, createWebHistory } from 'vue-router'
import EventList   from '../views/EventList.vue'
import EventDetail from '../views/EventDetail.vue'
import EventCreate from '../views/EventCreate.vue'
import EventEdit   from '../views/EventEdit.vue'

const routes = [
  { path: '/',                 component: EventList   },
  { path: '/events/new',       component: EventCreate },
  { path: '/events/:id',       component: EventDetail },
  { path: '/events/:id/edit',  component: EventEdit   }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
