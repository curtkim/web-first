<script setup>
import Child2 from '../components/Child2.vue' 
import Child3 from '../components/Child3.vue' 
import { ref, onMounted } from 'vue'

const child2= ref(null)
const child3= ref(null)

onMounted(() => {
  console.log('child2', child2.value)
  console.log('child3', child3.value)
  // chlid3는 return만으로는 property를 expose하지 못하는 것 같다.
  //child.value will hold an instance of <Child />
})
</script>

<template>
	<div>ComponentRef</div>
  <Child2 ref="child2"/>
  <Child3 ref="child3"/>
</template>

<style scoped>
</style>
