<script setup>
  import GuessCounts from '../components/GuessCounts.vue';
import { CategoriesService } from '../service/CategoriesService';
  import { onMounted, ref } from 'vue';

  let data = ref();

  onMounted(async () => {
    data.value = await new CategoriesService().getCategories();
  });
</script>

<template>
  <main>
    <ul class="collection">
      <li class="collection-item avatar" v-for="(category, index) in data">
        <i class="circle" style="margin-top: 1em" :title="'rating: ' + category.rating"><b>{{ index + 1}}</b></i>
        <h5>{{ category.name }}</h5>
        <h6>{{ category.questions }} questions</h6>
        <GuessCounts :correct="category.guesses.correct" :incorrect="category.guesses.incorrect" :unanswered="category.guesses.unanswered"/>
      </li>
    </ul>
  </main>
</template>
