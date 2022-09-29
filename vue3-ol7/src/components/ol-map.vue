<script setup>
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
//import VectorLayer from 'ol/layer/Vector';
//import VectorSource from 'ol/source/Vector';

import {DAUM_ROAD_MAP} from '../ol-daum.js'
 
import { ref, watch, onMounted, onUnmounted, provide } from 'vue'

function level2zoom(level){
  return 14-level + 3
}
const props = defineProps({
  center: Array, 
  level: Number,
})

const mapRef = ref(null)

let map = new Map({
  layers: [
    new TileLayer({
      source: DAUM_ROAD_MAP,
    }),
  ],
  view: new View({
    center: props.center,
    zoom: level2zoom(props.level)
  }),
});

watch(
  ()=> props.center, 
  (oldValue, newVaule)=>{
    map.getView().setCenter(newVaule)
  }
)
watch(
  ()=> props.level, 
  (oldValue, newVaule)=>{
    map.getView().setZoom(level2zoom(newVaule))
  }
)

onMounted(() => {
  map.setTarget(mapRef.value)
})
onUnmounted(()=>{
  map.setTarget(null)
  map = null;
})
provide('map', map)

</script>

<template>
  <div ref="mapRef" class="mapContainer">
    <slot></slot>
  </div>
</template>

<style scoped>
.mapContainer {
  width: 100%;
  height: 100%;
}
</style>
