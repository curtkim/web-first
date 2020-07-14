<template>
  <div>    
    <canvas id="deckcanvas"></canvas>
  </div>
</template>

<script>

import {Deck} from '@deck.gl/core';
import {COORDINATE_SYSTEM, PointCloudLayer} from 'deck.gl';

const INITIAL_VIEW_STATE = {
  latitude: 37.74,
  longitude: -122.4,
  zoom: 13,
  bearing: 0,
  pitch: 30
};

const URL = 'pointcloud.json'

export default {
  name: 'PointCloud',

  data() {
    return {
    };
  },
  beforeDestroy() {
    if (this.deck) this.deck.finalize();
  },    
  mounted() {

    this.deck = new Deck({
      canvas: "deckcanvas",
      initialViewState: INITIAL_VIEW_STATE,
      controller: true,
      layers: [
        new PointCloudLayer({
            id: 'point-cloud-layer',
            data: URL,
            pickable: false,
            coordinateSystem: COORDINATE_SYSTEM.METER_OFFSETS,
            coordinateOrigin: [-122.4, 37.74],
            radiusPixels: 4,
            getPosition: d => d.position,
            getNormal: d => d.normal,
            getColor: d => d.color,
            onHover: ({object, x, y}) => {
              const tooltip = object.position.join(', ');
              console.log(tooltip, x, y);
            }
          })
      ]
    });
  },
  methods: {
  }
}
</script>

<style scoped>

</style>
