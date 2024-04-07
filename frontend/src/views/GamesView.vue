<script setup>
  import { GamessService } from '../service/GamesService';
  import { onMounted, ref } from 'vue';

  let data = ref();

  onMounted(async () => {
    data.value = await new GamessService().getGames();
  });
</script>

<template>
  <main>
    <h1>Games</h1>
    <div v-for="game in data">
      <RouterLink :to="{name: 'game', params: {id : game.id}}">
        <pre>
          {{ JSON.stringify(game, null, 2) }}
        </pre>
      </RouterLink>
    </div>
  </main>
</template>
