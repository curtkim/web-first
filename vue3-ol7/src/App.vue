<script setup>
import olMap from './components/ol-map.vue'

import { ref } from 'vue'
//import HelloWorld from './components/HelloWorld.vue';
import OlVectorLayer from './components/ol-vector-layer.vue';
import Feature from 'ol/Feature';
import Polygon from 'ol/geom/Polygon';
import Point from 'ol/geom/Point';
import {Fill, RegularShape, Stroke, Style} from 'ol/style';
import samples1 from './sample1.json'
import samples2 from './sample2.json'

function makeFeatures(samples){
  return samples.map(it => {
    return new Feature({
      geometry: new Point([it.x, it.y]),
      time: it.time,
    })
  })
}

const level = ref(10);
const center = ref([195063,442898])

const features = ref();
features.value = makeFeatures(samples1)

/*
const feature = new Feature({
  geometry: new Polygon([[[0, 0], [200000, 0],[100000, 100000],[0, 0]]]),
  value: 10,
  name: 'My Polygon',
  color: '#00FF00',
});
const features = ref([feature])
*/

/*
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
*/

const stroke = new Stroke({color: 'black', width: 2});
const fill = new Fill({color: 'red'});
const triangle = new Style({
  image: new RegularShape({
    fill: fill,
    stroke: stroke,
    points: 3,
    radius: 10,
    rotation: 0, //Math.PI / 4,
    angle: 0,
  }),
})

function zoom(){
  level.value--
}
function changeSample(){
  features.value = makeFeatures(samples2)
}
</script>

<template>
  <div id="container">
    <div class="left">
      left<br/>
      {{ level }}
      <button @click="zoom">확대</button><br/>
      <button @click="changeSample">다음 경로</button>
    </div>
    <div class="map">
      <ol-map :center="center" :level="level">
        <ol-vector-layer :features="features" :style="triangle"/> 
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
