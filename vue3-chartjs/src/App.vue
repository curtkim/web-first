<script setup lang="ts">
import {ref} from 'vue'
import Vue3ChartJs from "@j-t-mcc/vue3-chartjs";
import 'chartjs-adapter-luxon';
import {DateTime} from 'luxon'
import {Chart, ChartEvent} from 'chart.js'
import {getRelativePosition} from 'chart.js/helpers'

const chartRef = ref(null)

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
  { x: DateTime.local(2000, 1, 1, 21, 8).toMillis(), y: 10 },
];

const lineChart = {
  type: "line",
  data: {
    datasets: [
      {
        label:'dataset1',
        data:data
      },
      {
        label: 'dataset2',
        data: data2
      }
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
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
    },
    interaction: {
      intersect: true,
      mode: 'x',
    },
    onClick: (e: ChartEvent) => {
      const points = chartRef.value.chartJSState.chart.getElementsAtEventForMode(e, 'nearest', { intersect: true }, true);

      if (points.length) {
        const firstPoint = points[0];
        console.log(firstPoint)
        //{element: PointElement, datasetIndex: 0, index: 1}
      }

      const canvasPosition = getRelativePosition(e, chartRef.value.chartRef);
      console.log(canvasPosition)
      //{type: 'click', chart: Chart, native: PointerEvent, x: 330, y: 244, …}

      const dataX = chartRef.value.chartJSState.chart.scales.x.getValueForPixel(canvasPosition.x);
      const dataY = chartRef.value.chartJSState.chart.scales.y.getValueForPixel(canvasPosition.y);
      console.log(dataX, dataY) // floating value로 출력된다.
    }
  },
};
 

function addData(){
  data.push(
    { x: DateTime.local(2000, 1, 1, 23, 9).toMillis(), y: 60 },
  )
  chartRef.value.update()
}
</script>

<template>
  <div class="container">
    <div class="header">
    </div>
    <div class="left">
      <button @click="addData">add data</button>
    </div>
    <div class="main">
      <vue3-chart-js ref="chartRef" v-bind="{...lineChart}"/>
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
