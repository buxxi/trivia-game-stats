<script setup>
    const props = defineProps(['name', 'avatar']);
    const avatarFiles = import.meta.glob('../assets/avatars/*.png', {
        eager: true,
        import: 'default',
        query: '?url'
    });
    const avatars = Object.fromEntries(
        Object.entries(avatarFiles).map(([path, url]) => [
            path.split('/').pop().replace(/\.png$/, ''),
            url
        ])
    );
</script>


<template>
    <img :src="avatars[props.avatar]" :title="props.name"/>
</template>
