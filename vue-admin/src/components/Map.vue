<template>
  <div id="map-holder">
    <extend-height>
    map
    </extend-height>
  </div>
</template>

<script>
import ExtendHeight from './ExtendHeight.vue'
export default {
  name: 'Map',
  components: {
    ExtendHeight
  },
  props: {

  }
}
</script>

<style scoped>
#map-holder {
  background-color: silver;
}
</style>
