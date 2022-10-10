<script setup lang="ts">
import { ref, PropType } from 'vue'
import type {CSSProperties} from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS, Title, Tooltip, Legend, BarElement,
  CategoryScale, LinearScale
} from 'chart.js'
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

defineProps<{
  chartId: {
    type: String,
    default: 'bar-chart'
  },
  cssClasses: {
    default: '',
    type: String
  },
  styles: {
    type: CSSProperties,
    default: () => {}
  }
  /*
  plugins: {
    type: Object as PropType<Plugin<'bar'>>,
    default: () => {}
  }
  */
}>()

const chartData = {
  labels: [ 'January', 'February', 'March' ],
  datasets: [ { data: [40, 20, 12] } ]
}
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
}

</script>

<template>
  <Bar
      :chart-options="chartOptions"
      :chart-data="chartData"
      :chart-id="chartId"
      :dataset-id-key="datasetIdKey"
      :plugins="plugins"
      :css-classes="cssClasses"
      :styles="styles"
  />
</template>

<style scoped>
</style>
