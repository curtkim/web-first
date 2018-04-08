<template>
  <div id="etc">
    <h3>etc</h3>
    <button v-on:click="newData()">new data</button>
    <chart-3d :options='chartOptions' :data='chartData'></chart-3d>
  </div>
</template>

<script lang="coffee">

makeData = (steps= 50)->
  chartData = new vis.DataSet()
  counter = 0
  # number of datapoints will be steps*steps
  axisMax = 314
  axisStep = axisMax / steps
  x = 0
  while x < axisMax
    y = 0
    while y < axisMax
      value = Math.sin(x / steps) * Math.cos(y / steps) * steps + 50
      chartData.add {
        id: counter++
        x: x
        y: y
        z: value
        style: value
      }
      y += axisStep
    x += axisStep
  chartData

module.exports = {
  name: 'etc'
  data: ()->
    {
      chartOptions:
        width:  '500px'
        height: '552px'
        style: 'surface'
        showPerspective: true
        showGrid: true
        showShadow: false
        keepAspectRatio: true
        verticalRatio: 0.5
      chartData: makeData()
    }
  methods:
    newData : ()->
      this.chartData = makeData(parseInt(Math.random()*99)+1)
}
</script>
