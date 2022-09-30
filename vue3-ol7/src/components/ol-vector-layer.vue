<script setup>
//import {createEmpty, extend} from 'ol/extent';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import { makeAllExtent } from '../ol-utils';

import { ref, watch, onMounted, onUnmounted, provide, inject } from 'vue'

const props = defineProps({
  style: Function | Object,
  features: Array, 
})

const map = inject('map')

const vectorLayer = new VectorLayer({
  source: new VectorSource({
    features: props.features,
  }),
  style: props.style,
});

watch(
  ()=> props.style, 
  (style, old)=>{
    vectorLayer.setStyle(style)
  }
)
watch(
  ()=> props.features, 
  (features, oldValues)=>{
    vectorLayer.getSource().clear()
    vectorLayer.getSource().addFeatures(features)
    map.getView().fit( makeAllExtent(features))
  }
)

onMounted(() => {
  map.addLayer(vectorLayer)
})
onUnmounted(()=>{
  map.removeLayer(vectorLayer)
})
provide('vectorLayer', vectorLayer)

</script>

<template>
  <div></div>
</template>

<style scoped>
.mapContainer {
  width: 100%;
  height: 100%;
}
</style>
