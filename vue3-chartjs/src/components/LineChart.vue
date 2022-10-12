<script setup lang="ts">
import { ref, watch } from 'vue'

import Vue3ChartJs from "@j-t-mcc/vue3-chartjs";
import 'chartjs-adapter-luxon';
import {ChartEvent} from 'chart.js'

const emit = defineEmits(['onClick'])

const props = defineProps<{
  chartData: {
    type: Object,
    required: true
  },
  scales: {
    type: Object,
  },
}>()


const chartRef = ref(null)
function getChart(){
  return chartRef.value.chartJSState.chart
}

watch(
  ()=> props.chartData, 
  (data, old)=>{
    let chart = getChart()
    chart.data = data
    chart.update()
  }
)

function onClick(e: ChartEvent){
  const points = getChart().getElementsAtEventForMode(e, 'nearest', { intersect: true }, true);

  if (points.length) {
    const firstPoint = points[0];
    emit('onClick', firstPoint)
    //{element: PointElement, datasetIndex: 0, index: 1}
  }

  /*
  const canvasPosition = getRelativePosition(e, chartRef.value.chartRef);
  console.log(canvasPosition)
  //{type: 'click', chart: Chart, native: PointerEvent, x: 330, y: 244, …}

  const dataX = chartRef.value.chartJSState.chart.scales.x.getValueForPixel(canvasPosition.x);
  const dataY = chartRef.value.chartJSState.chart.scales.y.getValueForPixel(canvasPosition.y);
  console.log(dataX, dataY) // floating value로 출력된다.
  */
}

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: props.scales,
  interaction: {
    intersect: true,
    mode: 'x',
  },
  onClick: onClick,
}

</script>

<template>
  <vue3-chart-js
    ref="chartRef"
    type="line"
    :data="props.chartData"
    :options="chartOptions"
  ></vue3-chart-js>
</template>

<style scoped>
</style>
