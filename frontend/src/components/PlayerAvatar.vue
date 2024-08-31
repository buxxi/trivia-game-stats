<script setup>
    let props = defineProps(['name', 'avatar']);
</script>


<template>
    <img :src="avatar.url" :title="name" style="height : 1em; margin : 0.15em"/>
</template>

