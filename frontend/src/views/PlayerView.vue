<script setup>
  import { PlayersService } from '../service/PlayersService';
  import { onMounted, ref, computed } from 'vue';
  import { useRoute } from 'vue-router';
  import GuessCounts from '../components/GuessCounts.vue';
  import DonutChart from '../components/DonutChart.vue';

  let route = useRoute();
  let data = ref();
  let avatarData = computed(() => data.value?.avatars.map(e => ({name: e.name, value: e.count})));
  let guessData = computed(() => data.value ? Object.entries(data.value.guesses).flatMap(e => ([{name: e[0].toUpperCase() + " correct", value: e[1].correct}, {name: e[0].toUpperCase() + " incorrect", value: e[1].incorrect}])) : undefined);
  let placementData = computed(() => data.value?.placements.map(e => ({name: placementToString(e.place), value: e.total})));

  onMounted(async () => {
    data.value = await new PlayersService().getPlayer(route.params.name);
  });

  function placementToString(placement) {
    switch (placement) {
      case 1:
        return "1st";
      case 2:
        return "2nd";
      case 3:
        return "3rd";
      default:
        return placement + "th";
    }
  }

</script>

<template>
  <main>
    <h1>{{data?.name}}</h1>
    <div v-if="data?.aliases.length > 0">Also known as: {{ data?.aliases }}</div>
    <div class="row">
      <div class="col s12 m3">
        <div class="card">
          <div class="card-body">
            <span class="card-title">Summary</span>
            <div>
              <ul class="collection">
                <li class="collection-item valign-wrapper"><i class="material-icons circle">access_time</i>&nbsp;Fastest guess:&nbsp;<b>{{ data?.fastestTime }}s</b></li>
                <li class="collection-item valign-wrapper"><i class="material-icons circle">access_time</i>&nbsp;Average time to guess:&nbsp;<b>{{ data?.averageTime }}s</b></li>
                <li class="collection-item valign-wrapper"><i class="material-icons circle">check</i>&nbsp;Total points won:&nbsp;<b>{{ data?.totalPointsWon }}</b></li>
                <li class="collection-item valign-wrapper"><i class="material-icons circle">do_not_disturb</i>&nbsp;Total points lost:&nbsp;<b>{{ data?.totalPointsLost }}</b></li>
                <li class="collection-item valign-wrapper"><i class="material-icons circle">clear</i>&nbsp;averageMultiplier:&nbsp;<b>{{ data?.averageMultiplier }}</b></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div class="col s12 m3">
        <div class="card">
          <div class="card-body">
            <span class="card-title">Avatars</span>
            <div><DonutChart :data="avatarData"/></div>
          </div>
        </div>
      </div>
      <div class="col s12 m3">
        <div class="card">
          <div class="card-body">
            <span class="card-title">Guesses</span>
            <div><DonutChart :data="guessData"/></div>
          </div>
        </div>
      </div>
      <div class="col s12 m3">
        <div class="card">
          <div class="card-body">
            <span class="card-title">Placements</span>
            <div><DonutChart :data="placementData"/></div>
          </div>
        </div>
      </div>
    </div>
    <h3>Categories:</h3>
    <ul class="collection">
      <li class="collection-item avatar" v-for="(category, index) in data?.categories">
        <i class="circle" style="margin-top: 1em" :title="'rating: ' + category.rating"><b>{{ index + 1}}</b></i>
        <h5>{{ category.name }}</h5>
        <GuessCounts :correct="category.correct" :incorrect="category.incorrect" :unanswered="category.unanswered"/>
      </li>
    </ul>
  </main>
</template>
