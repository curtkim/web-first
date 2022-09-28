<script setup>
import olMap from './components/ol-map.vue'

import { ref } from 'vue'
import HelloWorld from './components/HelloWorld.vue';
const level = ref(3);

function zoom(){
  level.value++
}
</script>

<template>
  <hello-world msg="test"/>
  <!--
  <div id="container">
    <div class="left">
      left<br/>
      {{ level }}
      <button @click="zoom">확대</button>
    </div>
    <div class="map">
      <ol-map :center="[195063,442898]" :level="level"/>
    </div>
  </div>
  -->
</template>

<style scoped>
  .left {
    position: absolute;
    width: 20%;
    height: 100%;
  }

  .map {
    position: absolute;
    width: 80%;
    height: 100%;
    left: 20%;
    background-color: #eee;
  }
</style>
