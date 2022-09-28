<script setup>
import { ref } from 'vue'

const a = 1
const b = ref(2)

defineExpose({
  a,
  b
})
</script>

<template>
  <div>
    Child2<br/>
    {{ b }}
  </div>
</template>

<style scoped>
</style>
