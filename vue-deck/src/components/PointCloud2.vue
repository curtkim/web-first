<template>
  <div>    
    <canvas id="deckcanvas"></canvas>
  </div>
</template>

<script>

import {Deck} from '@deck.gl/core';
import {COORDINATE_SYSTEM, PointCloudLayer, OrbitView} from 'deck.gl';
import {LASWorkerLoader} from '@loaders.gl/las';
import {PCDWorkerLoader} from '@loaders.gl/pcd';
import {OBJWorkerLoader} from '@loaders.gl/obj';
import {PLYWorkerLoader} from '@loaders.gl/ply';
import {registerLoaders} from '@loaders.gl/core';

registerLoaders(LASWorkerLoader);
registerLoaders(PCDWorkerLoader);
registerLoaders(OBJWorkerLoader);
registerLoaders(PLYWorkerLoader);

//const LAZ_SAMPLE = 'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-laz/indoor.0.1.laz';
//const PCD_SAMPLE = 'lidar.pcd';
//const OBJ_SAMPLE = '000001_points.obj';
const PLY_SAMPLE = 'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-ply/lucy800k.ply';

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
  name: 'PointCloud2',

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
          data: PLY_SAMPLE,
          onDataLoad: this._onLoad,
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          getPosition: function(d){ 
            console.log(d);
            return d.position;
          },
          getNormal: [0, 1, 0],
          getColor: [255, 255, 255],
          opacity: 0.5,
          pointSize: 5,
          pickablepickable: true,
        })
      ]
    });
  },
  methods: {
    _onLoad({header, loaderData}) {
      console.log("_onLoad")
      console.log(header)
      console.log(loaderData)      
      const {mins, maxs} = loaderData.header;

      if (mins && maxs) {
        const viewState= {
          ...INITIAL_VIEW_STATE,
          target: [(mins[0] + maxs[0]) / 2, (mins[1] + maxs[1]) / 2, (mins[2] + maxs[2]) / 2],
          zoom: Math.log2(window.innerWidth / (maxs[0] - mins[0])) - 1
        };
        console.log("onLoad view", viewState)
        this.viewState = viewState;
      }
    }
  }
}
</script>

<style scoped>

</style>
