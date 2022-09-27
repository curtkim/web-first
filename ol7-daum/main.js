import './style.css';
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import MousePosition from 'ol/control/MousePosition';
import {createStringXY} from 'ol/coordinate';
import {defaults as defaultControls} from 'ol/control';
import Style from 'ol/style/Style'
import Fill from 'ol/style/Fill'
import GeoJSON from 'ol/format/GeoJSON'

import {DAUM_ROAD_MAP} from './ol-daum.js'

const mousePositionControl = new MousePosition({
  coordinateFormat: createStringXY(4)
});

let geojsonObject = {
  'type': 'FeatureCollection',
  'features': [{
    'type': 'Feature',
    'geometry': {
      'type': 'Polygon',
      'coordinates': [
        [[0, 0], [200000, 0],[100000, 100000],[0, 0]]
      ]
    },
    'properties': {
      "value": 10
    }
  }]
};
let samples = new GeoJSON().readFeatures(geojsonObject)

const style = new Style({
  fill: new Fill({
    color: '#ff0000',
  }),
});

const vectorLayer = new VectorLayer({
  //background: '#1a2b39',
  source: new VectorSource({
    features: samples
  }),
  style: style,
});

const map = new Map({
  controls: defaultControls().extend([ mousePositionControl]),
  target: 'map',
  layers: [
    new TileLayer({
      source: DAUM_ROAD_MAP,
    }),
    vectorLayer,
  ],
  view: new View({
    center: [195063,442898],
    resolution: 1024
  }),
});
