<script setup>
import { RouterLink, RouterView } from 'vue-router';
import 'materialize-css/dist/css/materialize.min.css';
import 'material-icons/iconfont/material-icons.css';
</script>

<template>
  <header>
    <nav class="blue">
      <div class="nav-wrapper">
        <ul>
          <li><RouterLink to="/">Start</RouterLink></li>
          <li><RouterLink to="/players">Players</RouterLink></li>
          <li><RouterLink to="/categories">Categories</RouterLink></li>
          <li><RouterLink to="/games">Games</RouterLink></li>
        </ul>
      </div>
    </nav>
  </header>
  <RouterView />
</template>
