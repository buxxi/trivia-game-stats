<script setup>
  import { CategoriesService } from '../service/CategoriesService';
  import { onMounted, ref } from 'vue';

  let data = ref();

  onMounted(async () => {
    data.value = await new CategoriesService().getCategories();
  });
</script>

<template>
  <main>
    <h1>Categories</h1>
    <pre v-for="category in data">{{ JSON.stringify(category, null, 2) }}</pre>
  </main>
</template>
