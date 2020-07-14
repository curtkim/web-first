<template>
  <div id="app">    
    <canvas id="deckcanvas"></canvas>
  </div>
</template>

<script>

import {Deck} from '@deck.gl/core';
import {ScatterplotLayer} from '@deck.gl/layers';
import {COORDINATE_SYSTEM, PointCloudLayer} from 'deck.gl';

import {LASWorkerLoader} from '@loaders.gl/las';
import {registerLoaders} from '@loaders.gl/core';

// Additional format support can be added here, see
// https://github.com/uber-web/loaders.gl/blob/master/docs/api-reference/core/register-loaders.md
registerLoaders(LASWorkerLoader);

// Data source: kaarta.com
const LAZ_SAMPLE =
  'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-laz/indoor.0.1.laz';


//const transitionInterpolator = new LinearInterpolator(['rotationOrbit']);

const INITIAL_VIEW_STATE = {
  target: [0, 0, 0],
  rotationX: 0,
  rotationOrbit: 0,
  orbitAxis: 'Y',
  fov: 50,
  minZoom: 0,
  maxZoom: 10,
  zoom: 1
};

export default {
  name: 'App',
  components: {
  },

  data() {
    return {
      viewState: {
        ...INITIAL_VIEW_STATE,
      },
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
        new ScatterplotLayer({
          data: [
            {position: [-122.45, 37.8], color: [255, 0, 0], radius: 100}
          ],
          getColor: d => d.color,
          getRadius: d => d.radius
        }),
        new PointCloudLayer({
          id: 'laz-point-cloud-layer',
          data: LAZ_SAMPLE,
          onDataLoad: this._onLoad,
          coordinateSystem: COORDINATE_SYSTEM.IDENTITY,
          getNormal: [0, 1, 0],
          getColor: [255, 255, 255],
          opacity: 0.5,
          pointSize: 0.5
        })   
      ]
    });
  },
  methods: {
    _onLoad({header, loaderData, progress}) {
      // metadata from LAZ file header
      console.log(loaderData)
      console.log(header, progress)
      const {mins, maxs} = loaderData.header;

      if (mins && maxs) {
        // File contains bounding box info
        this.deck.setProps({        
          viewState: {
            ...this.viewState,
            target: [(mins[0] + maxs[0]) / 2, (mins[1] + maxs[1]) / 2, (mins[2] + maxs[2]) / 2],
            /* global window */
            zoom: Math.log2(window.innerWidth / (maxs[0] - mins[0])) - 1
          }
        });
      }

      // if (this.props.onLoad) {
      //   this.props.onLoad({count: header.vertexCount, progress: 1});
      // }
    },
  }
}
</script>

<style>
body {
  margin : 0;
  padding : 0;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin-top: 60px;
}

</style>
