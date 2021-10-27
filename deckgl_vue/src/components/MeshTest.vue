<template>
  <div>    
    <canvas id="deckcanvas"></canvas>
  </div>
</template>

<script>

import {Deck} from '@deck.gl/core';

import {
  COORDINATE_SYSTEM,
  OrbitView,
  DirectionalLight,
  LightingEffect,
  AmbientLight
} from '@deck.gl/core';
import {SolidPolygonLayer} from '@deck.gl/layers';
import {SimpleMeshLayer} from '@deck.gl/mesh-layers';

import {OBJLoader} from '@loaders.gl/obj';
import {registerLoaders} from '@loaders.gl/core';

// Add the loaders that handle your mesh format here
registerLoaders([OBJLoader]);

const MESH_URL =
  'https://raw.githubusercontent.com/visgl/deck.gl-data/master/examples/mesh/minicooper.obj';

const INITIAL_VIEW_STATE = {
  target: [0, 0, 0],
  rotationX: 0,
  rotationOrbit: 0,
  orbitAxis: 'Y',
  fov: 30,
  zoom: 0
};

const SAMPLE_DATA = (([xCount, yCount], spacing) => {
  const data = [];
  for (let x = 0; x < xCount; x++) {
    for (let y = 0; y < yCount; y++) {
      data.push({
        position: [(x - (xCount - 1) / 2) * spacing, (y - (yCount - 1) / 2) * spacing],
        color: [(x / (xCount - 1)) * 255, 128, (y / (yCount - 1)) * 255],
        orientation: [(x / (xCount - 1)) * 60 - 30, 0, -90]
      });
    }
  }
  //console.log(data);
  return data;
})([2, 2], 120);

const ambientLight = new AmbientLight({
  color: [255, 255, 255],
  intensity: 1.0
});

const dirLight = new DirectionalLight({
  color: [255, 255, 255],
  intensity: 1.0,
  direction: [-10, -2, -15],
  _shadow: true
});

const lightingEffect = new LightingEffect({ambientLight, dirLight});

const background = [
  [
    [-1000.0, -1000.0, -40], 
    [1000.0, -1000.0, -40], 
    [1000.0, 1000.0, -40], 
    [-1000.0, 1000.0, -40]
  ]
];


export default {
  name: 'MeshTest',

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
      views: new OrbitView({near: 0.1, far: 2}),
      initialViewState: this.viewState,
      controller: true,
      onViewStateChange: (props) => {
        const {viewState} = props;        
        this.viewState = viewState;
        this.$emit("viewStateChange", viewState);
      },      
      layers: [
        new SimpleMeshLayer({
          id: 'mini-coopers',
          data: SAMPLE_DATA,
          mesh: MESH_URL,
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          getPosition: d => d.position,
          getColor: d => d.color,
          getOrientation: d => d.orientation
        }),
        // only needed when using shadows - a plane for shadows to drop on
        new SolidPolygonLayer({
          id: 'background',
          data: background,
          extruded: false,
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          getPolygon: f => f,
          getFillColor: [0, 0, 0, 0]
        })
      ],
      effects: [lightingEffect],
    });
  },
  methods: {
  }
}
</script>

<style scoped>

</style>
