<script setup>
  import { PlayersService } from '../service/PlayersService';
  import GuessCounts from '../components/GuessCounts.vue';
  import { onMounted, ref } from 'vue';

  let data = ref();

  onMounted(async () => {
    data.value = await new PlayersService().getPlayers();
  });
</script>

<template>
  <main>
    <ul class="collection">
      <li class="collection-item avatar" v-for="(player, index) in data">
        <RouterLink :to="{name: 'player', params: {name : player.name}}" class="secondary-content"><i class="material-icons medium">chevron_right</i></RouterLink>
        <img class="circle" style="margin-top: 1em" :src="player.avatar.url" :title="player.avatar.name"/>
        <RouterLink :to="{name: 'player', params: {name : player.name}}"><h5>{{ player.name }}</h5></RouterLink>
        <div>
          <div>
            Guesses: <GuessCounts :correct="player.guesses.correct" :incorrect="player.guesses.incorrect" :unanswered="player.guesses.unanswered"/>
          </div>
          <div>
            Wins: <GuessCounts :correct="player.games.wins" :incorrect="player.games.total - player.games.wins" :unanswered="0"/>
          </div>
        </div>  
      </li>
    </ul>
  </main>
</template>
