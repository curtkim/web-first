<script setup>
import {ref, provide} from 'vue'
import Child from '../components/Child.vue'

const message = ref('hello')
provide('message', message)
</script>

<template>
  <input v-model="message">
  <Child/>
</template>

<style scoped>
</style>
