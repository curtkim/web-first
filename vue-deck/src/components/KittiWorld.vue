<template>
  <div>
    <ul>
      <li v-for="id in ids" :key="id">
        <a href="#" v-on:click="select(id)">{{ id }}</a>
      </li>
    </ul>
    <div>
      <img :src="imageUrl"/>
    </div>
    <canvas id="deckcanvas"></canvas>    
  </div>
</template>

<script>

const fetch = require('node-fetch');

import {Deck} from '@deck.gl/core';
import {COORDINATE_SYSTEM, OrbitView,   
  DirectionalLight,
  LightingEffect,
  AmbientLight,
  PointCloudLayer
} from 'deck.gl';
import {SimpleMeshLayer} from '@deck.gl/mesh-layers';
import {SolidPolygonLayer} from '@deck.gl/layers';

import {LASWorkerLoader} from '@loaders.gl/las';
import {PCDWorkerLoader} from '@loaders.gl/pcd';
import {OBJWorkerLoader} from '@loaders.gl/obj';
import {PLYWorkerLoader} from '@loaders.gl/ply';
import {registerLoaders} from '@loaders.gl/core';

registerLoaders(LASWorkerLoader);
registerLoaders(PCDWorkerLoader);
registerLoaders(OBJWorkerLoader);
registerLoaders(PLYWorkerLoader);

const IDS = [
  '000001',
  '000002',
];

const ground = [
  [
    [-10.0, -10.0, 0], 
    [10.0, -10.0, 0], 
    [10.0, 10.0, 0], 
    [-10.0, 10.0, 0]
  ]
];

const INITIAL_VIEW_STATE = {
  target: [0, 20, 0],
  rotationX: 0,
  rotationOrbit: 0,
  //orbitAxis: 'Y',
  fov: 50,
  zoom: 3.5
};


function parseObj(text){
  let arr = [];
  const lines = text.split("\n");
  for (let line of lines) {
    const parts = line.split(' ');
    const x = parseFloat(parts[1]);
    const y = parseFloat(parts[2]);
    const z = parseFloat(parts[3]);
    if( x){
      arr.push({position:[x, y, z]});
    }
  }
  return arr;
}

export default {
  name: 'KittiWorld',

  data() {
    return {
      ids: IDS,
      data_id: '000002',
      points:[],
      viewState: {
        ...INITIAL_VIEW_STATE,
      },      
    };
  },
  created() {
    this.fetch();
  },  
  beforeDestroy() {
    if (this.deck) this.deck.finalize();
  },    
  mounted() {
    this.deck = new Deck({
      canvas: "deckcanvas",
      height: '50%',
      views: new OrbitView({near: 0.1, far: 100}),
      initialViewState: this.viewState,
      controller: true,
      onViewStateChange: (props) => {
        const {viewState} = props;        
        this.viewState = viewState;
        this.$emit("viewStateChange", viewState);
      },
      layers: this.computedLayers,
    });
  },
  watch: {
    data_id: function(){
      this.fetch();
    },
  },
  methods: {
    fetch() {
      fetch(this.data_id + '_points.obj')
        .then(res => res.text())
        .then(body => {
          this.points = parseObj(body);
          this.update();
        });
    },
    update() {
      this.deck.setProps({
        layers: this.computedLayers,
      });
    },
    select(id){
      this.data_id = id;
    }
  },
  computed: {
    imageUrl() {
      return this.data_id + '.png';
    },
    computedLayers() {
      return [
        new PointCloudLayer({
          id: 'point-cloud-layer',
          data: this.points,
          pickable: false,
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          pointSize: 2,
          opacity: 0.9,
          getPosition: d => d.position,
          getNormal: [0, 1, 0],
          getColor: [255, 255,255],
          onHover: ({object, x, y}) => {
            const tooltip = object.position.join(', ');
            console.log(tooltip, x, y);
          }
        }),
        new SimpleMeshLayer({
          id: 'pred',
          visible: true,
          data: [{position:[0,0,0]}],
          mesh: this.data_id + '_pred.ply',
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          getPosition: d => d.position,
          getNormal: [0, 1, 0],
          getColor: [0, 0, 255],
        }),
        new SimpleMeshLayer({
          id: 'GT',
          visible: true,
          data: [{position:[0,0,0]}],
          mesh: this.data_id + '_gt.ply',
          coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
          getPosition: d => d.position,
          getNormal: [0, 1, 0],
          getColor: [0, 255, 0],
        }),
      ];
    }
  }
}
</script>

<style scoped>

canvas {
  border : 1px solid silver;
}
</style>
