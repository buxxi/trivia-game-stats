<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <nav>
      <RouterLink to="/">Start</RouterLink>
      <RouterLink to="/players">Players</RouterLink>
      <RouterLink to="/categories">Categories</RouterLink>
      <RouterLink to="/games">Games</RouterLink>
    </nav>
  </header>

  <RouterView />
</template>
