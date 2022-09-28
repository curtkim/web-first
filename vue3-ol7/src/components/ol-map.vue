<script setup>
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
//import VectorLayer from 'ol/layer/Vector';
//import VectorSource from 'ol/source/Vector';

import {DAUM_ROAD_MAP} from '../ol-daum.js'
 
import { ref, watch, onMounted } from 'vue'

function level2zoom(level){
  return 14-level
}
const props = defineProps({
  center: Array, 
  level: Number,
})

const mapContainer = ref(null)
var map;

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
watch(props, (oldValue, newVaule)=>{
  console.log('watch props', oldValue, newVaule)
})

onMounted(() => {
  map = new Map({
    target: mapContainer.value,
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
})

</script>

<template>
  <div ref="mapContainer" class="mapContainer"></div>
</template>

<style scoped>
.mapContainer {
  width: 100%;
  height: 100%;
}
</style>
