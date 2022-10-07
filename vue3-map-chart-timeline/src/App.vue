<script setup lang="ts">
import olMap from './components/ol-map.vue'

import {ref} from 'vue'
import Vue3ChartJs from '@j-t-mcc/vue3-chartjs'
import OlVectorLayer from './components/ol-vector-layer.vue';

import Feature from 'ol/Feature';
import {Point, Polygon} from 'ol/geom';
import {ComplexPoint} from './models'
import {sample2line} from "./convert";
import {lineStyle, triangle} from "./map-style";

import sample1Json from './sample1.json'
import sample2Json from './sample2.json'

const sample1: Array<ComplexPoint> = sample1Json
const sample2: Array<ComplexPoint> = sample2Json
let sampleTimeMap = new Map<number, ComplexPoint>()
sample1.forEach(pt => {
  sampleTimeMap.set(pt.time, pt)
})

const lineChart = {
  id: 'lineChart',
  type: 'line',
  data: {
    labels: ['1', '2', '3', '4'],
    datasets: [
      {
        label: 'test',
        data: [40, 20, 80, 10]
      }
    ]
  },
  options: {
    responsive: true,
    maintainAspectRatio: false, // default is `true`, default `aspectRatio` is 2
  }
}


function makeFeatures(samples: Array<ComplexPoint>) {
  return samples.map(it => {
    return new Feature({
      geometry: new Point([it.x, it.y]),
      time: it.time,
    })
  })
}

const level = ref(5);
const center = ref([195063, 442898])

// const features = ref();
// features.value = makeFeatures(sample1)

const currLines = ref()
currLines.value = [
  new Feature({
    geometry: sample2line(sample1),
    name: 'currLine'
  })
]
const currPts = ref()
currPts.value = []


function zoom() {
  level.value--
}

// function changeSample(){
//   features.value = makeFeatures(sample2)
// }
function highlightPoint(time: number) {
  const pt: ComplexPoint | undefined = sampleTimeMap.get(time)
  if (pt)
    center.value = [pt.x, pt.y]
}

function enterPoint(time: number) {
  const pt: ComplexPoint | undefined = sampleTimeMap.get(time)
  if (pt) {
    currPts.value = [
      new Feature({
        geometry: new Point([pt.x, pt.y]),
        name: 'currPt'
      })
    ]
  }
}

</script>

<template>
  <div id="container">
    <div class="header">
      sample1
    </div>

    <div class="left">
      <button @click="resizeChart">resize</button>
      {{ level }}
      <button @click="zoom">확대</button>
      <br/>
      <button @click="changeSample">다음 경로</button>
      <div>
        <table>
          <tr v-for="(pt, idx) in sample1" :key="pt.time" @click="highlightPoint(pt.time)"
              @mouseenter="enterPoint(pt.time)">
            <td>{{ idx }} {{ pt.time }}</td>
          </tr>
        </table>
      </div>
    </div>

    <div class="map">
      <ol-map :center="center" :level="level">
        <!--
        <ol-vector-layer :features="features" :style="triangle"/>
        -->
        <ol-vector-layer :features="currLines" :style="lineStyle"/>
        <ol-vector-layer :features="currPts" :style="triangle"/>
      </ol-map>
    </div>

    <div class="chart">
      <vue3-chart-js
          :id="lineChart.id"
          :type="lineChart.type"
          :data="lineChart.data"
          :options="lineChart.options"
      >
      </vue3-chart-js>
    </div>
  </div>
</template>

<style scoped>
#container {
  height: 100vh;
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  grid-template-rows: 80px minmax(0, 3fr) minmax(0, 1fr);
  grid-template-areas:
          "header header"
          "detail map"
          "detail chart";
}

.header {
  grid-area: header;
}
.left {
  grid-area: detail;
  overflow-y: scroll;
}
.map {
  grid-area: map;
}
.chart {
  grid-area: chart;
}
</style>
