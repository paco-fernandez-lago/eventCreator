<template>
  <div id="app">
    <nav class="navbar">
      <router-link to="/" class="navbar-brand">📅 Event Creator</router-link>
      <router-link to="/" class="navbar-link">Todos los eventos</router-link>
    </nav>
    <main class="container">
      <router-view />
    </main>
  </div>
</template>
