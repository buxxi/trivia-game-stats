<script setup>
  import { PlayersService } from '../service/PlayersService';
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';

  let route = useRoute();
  let data = ref();

  onMounted(async () => {
    data.value = await new PlayersService().getPlayer(route.params.name);
  });
</script>

<template>
  <main>
    <h1>Player: {{data?.name}}</h1>
    <pre>{{ JSON.stringify(data, null, 2) }}</pre>
  </main>
</template>
