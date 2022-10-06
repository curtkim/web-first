<script setup>
import { ref } from 'vue'
import usePropsAsObjectProperties from '../usePropsAsObjectProperties.js'

const props = defineProps({
  msg: String
})

const {
  properties
} = usePropsAsObjectProperties(props)

console.log('props', props)
console.log('properties', properties)
console.log('props.msg', props.msg)

const count = ref(0)
</script>

<template>
  <h1>{{ msg }}</h1>

  <div class="card">
    <button type="button" @click="count++">count is {{ count }}</button>
  </div>

</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
