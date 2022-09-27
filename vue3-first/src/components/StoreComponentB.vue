<script setup>
import { store } from '../store.js'

</script>

<template>
  <div>
    <button @click="store.increment()">
      From B: {{ store.count }}
    </button>
  </div>
</template>

