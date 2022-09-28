<script setup>

const props = defineProps({
  value: Number
})

console.log("init value", props.value)
function increment(){
  props.value = props.value + 1
  // Set operation on key "value" failed: target is readonly
}
</script>

<template>
  <button @click="increment">+</button>
</template>

<style scoped>
</style>
