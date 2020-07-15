<template>
  <div>    
    <canvas id="deckcanvas"></canvas>
  </div>
</template>

<script>

import {Deck} from '@deck.gl/core';
import {COORDINATE_SYSTEM, PointCloudLayer, OrbitView} from 'deck.gl';

const INITIAL_VIEW_STATE = {
  target: [0, 0, 0],
  rotationX: 0,
  rotationOrbit: 0,
  orbitAxis: 'Y',
  fov: 50,
  minZoom: 0,
  maxZoom: 10,
  zoom: 5
};

const DATA = [
    {"position":[0,0,0]},
    {"position":[1,0,0]},
    {"position":[0,1,0]},
    {"position":[1,1,0]},
]

export default {
  name: 'PointCloud3',
  data() {
    return {
      viewState: {
        ...INITIAL_VIEW_STATE,
      }      
    };
  },
  beforeDestroy() {
    if (this.deck) this.deck.finalize();
  },    
  mounted() {
    this.deck = new Deck({
      canvas: "deckcanvas",
      views: new OrbitView(),
      initialViewState: this.viewState,
      controller: true,
      onViewStateChange: (props) => {
        const {viewState} = props;        
        this.viewState = viewState;
        this.$emit("viewStateChange", viewState);
      },      
      layers: [
        new PointCloudLayer({
          id: 'laz-point-cloud-layer',
          data: DATA,
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          getPosition: function(d){ 
            return d.position;
          },
          getNormal: [0, 1, 0],
          getColor: [255, 0,0],
          opacity: 1,
          pointSize: 10,
          pickable: true,
          onHover: ({object, x, y}) => {
            //const tooltip = object.position.join(', ');
            console.log(object, x, y);
          },

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
