import {Deck, COORDINATE_SYSTEM} from '@deck.gl/core';
import {GeoJsonLayer} from '@deck.gl/layers';
import {SimpleMeshLayer} from '@deck.gl/mesh-layers';
import {OBJLoader} from '@loaders.gl/obj';
import {registerLoaders} from '@loaders.gl/core';

// Add the loaders that handle your mesh format here
registerLoaders([OBJLoader]);

const MESH_URL =
  'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/mesh/minicooper.obj';

const INITIAL_VIEW_STATE = {
  latitude: 37.394687,
  longitude: 127.109748,
  zoom: 18.3,
  bearing: 0,
  pitch: 70,
  maxPitch: 75,
};


export const deck = new Deck({
  canvas: 'my_canvas',
  initialViewState: INITIAL_VIEW_STATE,
  controller: true,
  layers: [
    new GeoJsonLayer({
      id: 'lane',
      data: 'lane_wgs.geojson',
      coordinateSystem: COORDINATE_SYSTEM.LNGLAT,
      // Styles
      stroked: true,
      filled: false,
      lineWidthMinPixels: 1,
      opacity: 0.4,
      getLineColor: [60, 60, 60],
      getFillColor: [200, 200, 200]
    }),
    new SimpleMeshLayer({
      id: 'mini-coopers',
      data: [
        {
          position: [127.109748, 37.394687, 65],
          angle: 0,
          color: [0, 0, 0]
        }  
      ],
      mesh: MESH_URL,
      sizeScale: 1/40,
      wireframe: true,
      coordinateSystem: COORDINATE_SYSTEM.LNGLAT,
      getPosition: d => d.position,
      getColor: d => d.color,
      getOrientation: d => [0, d.angle-180, 0]
    })    
  ]
});

