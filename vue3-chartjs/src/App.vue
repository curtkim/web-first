<script setup lang="ts">
import {ref} from 'vue'
import {DateTime} from 'luxon'
import LineChart from './components/LineChart.vue'


let data = [
  { x: DateTime.local(2000, 1, 1, 0, 5).toMillis(), y: 0 },
  { x: DateTime.local(2000, 1, 1, 4, 11).toMillis(), y: 10 },
  { x: DateTime.local(2000, 1, 1, 12, 38).toMillis(), y: 20 },
  { x: DateTime.local(2000, 1, 1, 15, 18).toMillis(), y: 30 },
  { x: DateTime.local(2000, 1, 1, 18, 48).toMillis(), y: 40 },
  { x: DateTime.local(2000, 1, 1, 22, 8).toMillis(), y: 50 },
];

let data2 = [
  { x: DateTime.local(2000, 1, 1, 0, 5).toMillis(), y: 10 },
  { x: DateTime.local(2000, 1, 1, 5, 11).toMillis(), y: 50 },
  { x: DateTime.local(2000, 1, 1, 11, 38).toMillis(), y: 40 },
  { x: DateTime.local(2000, 1, 1, 16, 18).toMillis(), y: 30 },
  { x: DateTime.local(2000, 1, 1, 19, 48).toMillis(), y: 20 },
];

const lineChartData = ref({
  datasets: [
    {
      label: 'dataset1',
      data: data
    },
  ],
})

const scales ={
  x: {
    type: 'time',
    ticks: {
      source: 'data'
    },
    time: {
      unit: "minute",
      tooltipFormat: "mm:ss:SSS",
      displayFormats: {
        minute: 'mm:ss',
      }
    },
  }
}

function updateData(){
  lineChartData.value = {
    datasets:[
      {label: 'dataset2', data: data2}
    ]
  }
}
function lineChartElementClick(point){
  console.log(point)
}
</script>

<template>
  <div class="container">
    <div class="header">
    </div>
    <div class="left">
      <button @click="updateData">add data</button>
    </div>
    <div class="main">
      <line-chart :chartData="lineChartData" :scales="scales" @onClick="lineChartElementClick"/>
    </div>
    <div class="chart">
    </div>
  </div>
</template>

<style scoped>
div.container {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  grid-template-rows: 50px 5fr minmax(0, 3fr);
  grid-template-areas:
    "header header"
    "left main"
    "left chart";
  height: 100vh;
}
div.header{
  grid-area: header;
  background-color: #747bff;
}
div.left{
  grid-area: left;
  background-color: cadetblue;
}
div.main {
  grid-area: main;
}
div.chart{
  grid-area: chart;
  padding: 50px;
}
</style>
