<script setup>
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import { GamessService } from '../service/GamesService';
  import PlayerAvatar from '../components/PlayerAvatar.vue';

  let route = useRoute();
  let data = ref();

  onMounted(async () => {
    data.value = await new GamessService().getGame(route.params.id);
  });
</script>

<template>
  <main>
    <h1>Game: {{data?.id}}</h1>
    <h3>Players</h3>
    <div class="row">
      <div class="col s24 m3" v-for="(player, index) in data?.players">
        <div class="card horizontal">
          <div class="card-image">
            <img style="margin-top: 1em" :src="player.avatar.url" :title="player.avatar.name"/>
          </div>
          <div class="card-content">
            <span class="card-title"><b>{{ player.place }}:</b> {{ player.name }}</span>
            <span>Points: {{ player.points }}</span>
          </div>
        </div>
      </div>
    </div>
    <h3>Questions</h3>
    <ul class="collection">
      <li class="collection-item" v-for="(question, index) in data?.questions">
        <span class="badge">{{ question.category }}</span>  
        <h5>{{index + 1}}: {{ question.question }}</h5>
        <div class="row">
          <div class="col s12 m3">
            <div class="card" :class="{ green: question.answers[0].correct}">
              <div class="card-content">
                <span class="card-title activator"><b>A:</b> {{ question.answers[0].answer }}</span>
                <span style="font-size: 2em" :style="{marginRight: ( playerIndex * 2) +'em'}" class="btn-floating halfway-fab white" v-for="(player, playerIndex) in question.answers[0].playersGuessed.map(p => data.players.find(p2 => p2.name == p))">
                  <PlayerAvatar :name="player.name" :avatar="player.avatar"/>
                </span>
              </div>
            </div>
          </div>
          <div class="col s12 m3">
            <div class="card" :class="{ green: question.answers[1].correct}">
              <div class="card-content">
                <span class="card-title activator"><b>B:</b> {{ question.answers[1].answer }}</span>
                <span style="font-size: 2em" :style="{marginRight: ( playerIndex * 2) +'em'}" class="btn-floating halfway-fab white" v-for="(player, playerIndex) in question.answers[1].playersGuessed.map(p => data.players.find(p2 => p2.name == p))">
                  <PlayerAvatar :name="player.name" :avatar="player.avatar"/>
                </span>
              </div>
            </div>
          </div>
          <div class="col s12 m3">
            <div class="card" :class="{ green: question.answers[2].correct}">
              <div class="card-content">
                <span class="card-title activator"><b>C:</b> {{ question.answers[2].answer }}</span>
                <span style="font-size: 2em" :style="{marginRight: ( playerIndex * 2) +'em'}" class="btn-floating halfway-fab white" v-for="(player, playerIndex) in question.answers[2].playersGuessed.map(p => data.players.find(p2 => p2.name == p))">
                  <PlayerAvatar :name="player.name" :avatar="player.avatar"/>
                </span>
              </div>
            </div>
          </div>
          <div class="col s12 m3">
            <div class="card" :class="{ green: question.answers[3].correct}">
              <div class="card-content">
                <span class="card-title activator"><b>D:</b> {{ question.answers[3].answer }}</span>
                <span style="font-size: 2em" :style="{marginRight: ( playerIndex * 2) +'em'}" class="btn-floating halfway-fab white" v-for="(player, playerIndex) in question.answers[3].playersGuessed.map(p => data.players.find(p2 => p2.name == p))">
                  <PlayerAvatar :name="player.name" :avatar="player.avatar"/>
                </span>
              </div>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </main>
</template>
