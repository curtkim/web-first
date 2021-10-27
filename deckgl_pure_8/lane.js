import {Deck} from '@deck.gl/core';
import {GeoJsonLayer, ArcLayer} from '@deck.gl/layers';
import {COORDINATE_SYSTEM, OrbitView} from '@deck.gl/core';

const INITIAL_VIEW_STATE = {
  target: [0,0,0],
  rotationX: 85,
  rotationOrbit: 0,
  //orbitAxis: 'Y',
  fov: 70,
  zoom: 2
};
// 37.3946605563488, 127.10971189906581


export const deck = new Deck({
  canvas: 'my_canvas',
  views: new OrbitView({near: 0.1, far: 450}),
  initialViewState: INITIAL_VIEW_STATE,
  controller: true,
  layers: [
    new GeoJsonLayer({
      id: 'geojson-layer',
      data: 'lane.geojson',
      pickable: false,
      stroked: true,
      filled: false,
      extruded: false,
      lineWidthScale: 0.2,
      lineWidthMinPixels: 1,
      getFillColor: [160, 160, 180],
      getLineColor: [100, 0, 0],
      getRadius: 1,
      getLineWidth: 1,
      getElevation: 30
    }),
  ]
});

// For automated test cases
/* global document */
//document.body.style.margin = '0px';
