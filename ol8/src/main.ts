import './style.css'

import Map from 'ol/Map.js';
import TileLayer from 'ol/layer/Tile.js';
import View from 'ol/View.js';
import {DAUM_ROAD_MAP} from './ol-kakao.ts'


// @ts-ignore
const map = new Map({
  target: 'map',
  layers: [
    new TileLayer({
      source: DAUM_ROAD_MAP,
    }),
  ],
  view: new View({
    center: [195063,442898],
    resolution: 1024
  }),
});

