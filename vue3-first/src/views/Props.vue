<script setup>
import { ref, reactive } from 'vue'
import Increment from '../components/Increment.vue';

const count = ref(10)
</script>

<template>
	<div>Props</div>
	<div>{{ count }} </div>
  <Increment :value="count"/>
</template>

<style scoped>
</style>
