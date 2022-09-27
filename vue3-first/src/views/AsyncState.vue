<script setup>
import {ref, computed } from 'vue'
import {useFetch} from '../composable/useFetch.js'

const baseUrl = 'https://jsonplaceholder.typicode.com/todos/'
const id = ref(1)
const url = computed(()=> baseUrl + id.value)

const {data, error, retry} = useFetch(url)
</script>

<template>
  Load post id:
  <button v-for="i in 5" @click="id = i">{{ i }}</button>
  <div v-if="error">
    <p>Oop! Error encountered: {{ error.message }}</p>
    <button @click="retry">retry</button>
  </div>
  <div v-else-if="data">
    Data loaded: <pre> {{ data }}</pre>
  </div>
  <div v-else>Loading...</div>
</template>

<style scoped>
</style>
