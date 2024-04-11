<script setup>
  import { StatisticsService } from '../service/StatisticsService';
  import { onMounted, ref } from 'vue';

  let data = ref();

  onMounted(async () => {
    data.value = await new StatisticsService().getStatistics();
  });
</script>

<template>
  <main>
    <ul class="collection">
      <li class="collection-item avatar valign-wrapper">
        <i class="material-icons circle red">person</i>
        <h5>Unique players: <b>{{ data?.players }}</b></h5>
      </li>
      <li class="collection-item avatar valign-wrapper">
        <i class="material-icons circle blue">question_mark</i>
        <h5>Questions asked: <b>{{ data?.questions }}</b></h5>
      </li>
      <li class="collection-item avatar valign-wrapper">
        <i class="material-icons circle green">category</i>
        <h5>Different categories: <b>{{ data?.categories }}</b></h5>
      </li>
      <li class="collection-item avatar valign-wrapper">
        <i class="material-icons circle orange">access_time</i>
        <h5>Total amount of minutes played: <b>{{ data?.durationMinutes }}</b></h5>
      </li>
    </ul>
  </main>
</template>
