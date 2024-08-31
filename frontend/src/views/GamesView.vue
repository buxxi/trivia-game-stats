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
    <ul class="collection">
      <li class="collection-item avatar" v-for="(game, index) in data">
        <RouterLink :to="{name: 'game', params: {id : game.id}}" class="secondary-content"><i class="material-icons medium">chevron_right</i></RouterLink>
        <div>
          <h5>{{ game.players }} players @ {{ new Date(Date.parse(game.started)).toDateString() }} with {{ game.winner.name }} as winner</h5>
        </div>
      </li>
    </ul>
  </main>
</template>
