import {Deck} from '@deck.gl/core';
import {GridLayer} from '@deck.gl/layers';
import Map from './mapbox';

const DATA =
  'sf-bike-parking.json'; //eslint-disable-line
// Set your mapbox token here
const MAPBOX_TOKEN = process.env.MapboxAccessToken; // eslint-disable-line

const INITIAL_VIEW_STATE = {
  latitude: 37.78346622,
  longitude: -122.42177834,
  zoom: 11.5,
  bearing: 0,
  pitch: 60
};

const map = new Map({
  mapboxApiAccessToken: MAPBOX_TOKEN,
  container: 'map',
  style: 'mapbox://styles/mapbox/light-v9',
  viewState: INITIAL_VIEW_STATE
});

export const deck = new Deck({
  canvas: 'deck-canvas',
  width: '100%',
  height: '100%',
  initialViewState: INITIAL_VIEW_STATE,
  controller: true,
  onViewStateChange: ({viewState}) => {
    console.log(viewState)
    map.setProps({viewState});
  },
  layers: [
    new GridLayer({
      data: DATA,
      pickable: true,
      extruded: true,
      cellSize: 200,
      elevationScale: 4,
      getPosition: d => d.COORDINATES,
      onHover: ({object}) => setTooltip(`${object.position.join(', ')}\nCount: ${object.count}`)
    })
  ]
});
