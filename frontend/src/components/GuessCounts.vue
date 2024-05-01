<script setup>
    import { Bar } from 'vue-chartjs';
    import { Chart as ChartJS, BarElement, Tooltip, Legend, Colors, CategoryScale, LinearScale, Title } from 'chart.js';
    import { computed, defineProps } from 'vue';

    let props = defineProps(['correct', 'incorrect', 'unanswered']);

    ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, Colors);

    let loaded = computed(() => {
      return !!props.correct || !!props.incorrect || !!props.unanswered;
    });

    let chartData = computed(() => {
      if (!props.correct && !props.incorrect && !props.unanswered) {
        return {
            labels: [''],
            datasets: [
                { label: 'Example', data: [10] }
            ]
        };
      }

      return {
        labels: [''],
        datasets: [
            { label: 'Correct', data: [props.correct] },
            { label: 'Incorrect', data: [props.incorrect] },
            { label: 'Unanswered', data: [props.unanswered] }
        ]
      };
    });

    let total = computed(() => chartData.value.datasets.flatMap(e => e.data).reduce((total, cur) => total + cur));

    let options = {
      responsive: true,
      maintainAspectRatio: false,
      indexAxis: 'y',
      plugins: {
        legend: {
            display : false
        },
        tooltip: {
          callbacks: {
            label(context) {
                let label = chartData.value.datasets[context.datasetIndex].label;
                let value = ((context.raw / total.value) * 100).toFixed(1);
                return `${label}: ${context.raw} (${value}%)`;
            },
          },
        },
      },
      layout: {
        padding: {
          top : 5,
          bottom : 5
        }
      },
      scales: {
        x: {
            display : false,
            min: 0,
            max: total.value,
            grid: {
                display : false
            },
            stacked: true
        },
        y: {
            display : false,
            grid: {
                display : false
            },
            stacked: true
        }
      }
    };
</script>


<template>
    <div class="guesses">
        <Bar v-if="loaded" :data="chartData" :options="options"/>
    </div>
</template>

<style>
    .guesses {
        height : 2em !important;
        width : 100%;
    }

    .guesses canvas {
        border-radius: 1em;
    }
</style>