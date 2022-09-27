<script setup>
  import {ref} from 'vue'

  const center = ref([128, 37])
  const projection = ref('EPSG:4326')
  const zoom = ref(8)
  const rotation = ref(0)

</script>

<template>
  <div class="container">

    <div style="position: absolute; border: 1px solid silver; width: 10%; height: 100%;">
      List
    </div>

    <ol-map ref="map" :loadTilesWhileAnimating="true" :loadTilesWhileInteracting="true" 
        style="position: absolute; left:10%; height:100%; width:90%;">

        <ol-view ref="view" :center="center" :rotation="rotation" :zoom="zoom" 
        :projection="projection" />

        <ol-tile-layer>
          <ol-source-osm />
        </ol-tile-layer>
        
    </ol-map>
  </div>
</template>

<style scoped>
.container {
  position: block;
}

.overlay-content {
    background: red !important;
    color: white;
    box-shadow: 0 5px 10px rgb(2 2 2 / 20%);
    padding: 10px 20px;
    font-size: 16px;
}

</style>
