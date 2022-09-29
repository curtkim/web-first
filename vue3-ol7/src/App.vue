<script setup>
import olMap from './components/ol-map.vue'

import { ref } from 'vue'
//import HelloWorld from './components/HelloWorld.vue';
import OlVectorLayer from './components/ol-vector-layer.vue';
import Feature from 'ol/Feature';
import Polygon from 'ol/geom/Polygon';

import Style from 'ol/style/Style'
import Fill from 'ol/style/Fill'

const level = ref(10);
const center = ref([195063,442898])

const feature = new Feature({
  geometry: new Polygon([[[0, 0], [200000, 0],[100000, 100000],[0, 0]]]),
  value: 10,
  name: 'My Polygon',
  color: '#00FF00',
});
const features = ref([feature])

const style = new Style({
  fill: new Fill({
    color: '#ee0000',
  }),
});
const styleFunction = function (feature) {
  const color = feature.get('color') || '#ff0000'
  style.getFill().setColor(color);
  return style;
}

function zoom(){
  level.value--
}
</script>

<template>
  <div id="container">
    <div class="left">
      left<br/>
      {{ level }}
      <button @click="zoom">확대</button>
    </div>
    <div class="map">
      <ol-map :center="center" :level="level">
        <ol-vector-layer :features="features" :style="styleFunction"/> 
      </ol-map>
    </div>
  </div>
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
