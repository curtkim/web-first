<template>
  <svg width='200' height='200'>
    <circle :r='radius' cx='50' cy='50' style='stroke: gray; fill: white;'></circle>
  </svg>
</template>

<script type='text/babel'>
export default {
  name: 'svg-circle',
  props: ['radius']
}
</script>