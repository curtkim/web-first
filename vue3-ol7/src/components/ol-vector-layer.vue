<script setup>
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';

import { ref, watch, onMounted, onUnmounted, provide, inject } from 'vue'

const props = defineProps({
  style: Function,
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
  (values, oldValues)=>{
    vectorLayer.getSource().clear()
    vectorLayer.getSource().addFeatures(values)
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
