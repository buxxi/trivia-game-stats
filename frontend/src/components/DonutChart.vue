<script setup>
    import { Doughnut } from 'vue-chartjs';
    import { Chart as ChartJS, ArcElement, Tooltip, Legend, Colors } from 'chart.js';
    import { computed, defineProps } from 'vue';

    ChartJS.register(ArcElement, Tooltip, Legend, Colors);

    let props = defineProps(['data', 'colors']);

    let loaded = computed(() => {
      return !!props.data;
    });

    let chartData = computed(() => {
      if (!props.data) {
        return [{name: 'example', value : 0}];
      }

      return {
        labels: props.data?.map(e => e.name),
        datasets: [ { data: props.data?.map(e => e.value), backgroundColor : props.colors } ]    
      }
    });

    let options = {
      responsive: true,
      plugins: {
        tooltip: {
          callbacks: {
            label(context) {
              let total = context.dataset.data.reduce((total, cur) => total + cur);
              let value = ((context.raw / total) * 100).toFixed(1);
              return `${context.raw} (${value}%)`;
            },
          },
        },
      }
    };
</script>

<template>
  <Doughnut v-if="loaded" :data="chartData" :options="options"/>
</template>