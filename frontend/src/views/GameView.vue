<script setup>
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import { GamessService } from '../service/GamesService';

  let route = useRoute();
  let data = ref();

  onMounted(async () => {
    data.value = await new GamessService().getGame(route.params.id);
  });
</script>

<template>
  <main>
    <h1>Game: {{data?.id}}</h1>
    <pre>{{ JSON.stringify(data, null, 2) }}</pre>
  </main>
</template>
