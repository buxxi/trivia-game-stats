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
    <h1>Start</h1>
    <pre>{{ JSON.stringify(data, null, 2) }}</pre>
  </main>
</template>
