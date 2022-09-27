<script setup>
import {inject} from 'vue'
const message = inject('message')

</script>

<template>
  <p>Message to grand child {{ message }}</p>
</template>

<style scoped>
</style>
