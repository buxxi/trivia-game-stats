<script setup>
  import { PlayersService } from '../service/PlayersService';
  import { onMounted, ref, computed } from 'vue';
  import { useRoute } from 'vue-router';
  import GuessCounts from '../components/GuessCounts.vue';
  import DonutChart from '../components/DonutChart.vue';

  let route = useRoute();
  let data = ref();
  let newAlias = ref();
  let avatarData = computed(() => data.value?.avatars.map(e => ({name: e.name, value: e.count})));
  let guessData = computed(() => data.value ? Object.entries(data.value.guesses).flatMap(e => ([{name: e[0].toUpperCase() + " correct", value: e[1].correct}, {name: e[0].toUpperCase() + " incorrect", value: e[1].incorrect}])) : undefined);
  let placementData = computed(() => data.value?.placements.map(e => ({name: placementToString(e.place), value: e.total})));
  let guessColors = [
    "#ff2600", "#cc1e00",
    "#228b22", "#186218",
    "#1e90ff", "#0077ea",
    "#ccb805", "#9a8b04"
  ];

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

  function deleteAlias(alias) {
    data.value.aliases.splice(data.value.aliases.indexOf(alias), 1);
    new PlayersService().setAliases(route.params.name, data.value.aliases);
  }

  function addNewAlias() {
    data.value.aliases.push(newAlias.value);
    newAlias.value = "";
    new PlayersService().setAliases(route.params.name, data.value.aliases);
  }

</script>

<template>
  <main>
    <h1>{{data?.name}}</h1>
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
        <div class="card">
          <span class="card-title">Aliases</span>
          <div>
            <ul class="collection">
                <li class="collection-item" v-for="(alias) in data?.aliases">
                    <div>
                        {{ alias }}
                        <a href="#" @click.prevent="deleteAlias(alias)" class="secondary-content"><i class="material-icons trash red-text">delete</i></a>
                    </div>
                </li>
                <li class="collection-item" v-if="data?.aliases.length == 0"><i>None</i></li>
                <li class="collection-item">
                    <form class="input-field row">
                        <input type="text" placeholder="Enter new alias" class="col s10" v-model="newAlias" />
                        <button @click.prevent="addNewAlias" class="btn btn-small col green"><i class="material-icons">add</i></button>
                    </form>
                </li>
            </ul>
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
            <div><DonutChart :data="guessData" :colors="guessColors"/></div>
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
