<script setup>
  import { PlayersService } from '../service/PlayersService';
  import { onMounted, ref } from 'vue';

  let data = ref();

  onMounted(async () => {
    data.value = await new PlayersService().getPlayers();
  });
</script>

<template>
  <main>
    <h1>Players</h1>
    <div v-for="player in data">
      <RouterLink :to="{name: 'player', params: {name : player.name}}">
        <pre>
          {{ JSON.stringify(player, null, 2) }}
        </pre>
      </RouterLink>
    </div>
  </main>
</template>
