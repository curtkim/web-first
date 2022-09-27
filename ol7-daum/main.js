import './style.css';
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import MousePosition from 'ol/control/MousePosition';
import {createStringXY} from 'ol/coordinate';
import {defaults as defaultControls} from 'ol/control';
import {DAUM_ROAD_MAP} from './ol-daum.js'

const mousePositionControl = new MousePosition({
  coordinateFormat: createStringXY(4)
});

const map = new Map({
  controls: defaultControls().extend([ mousePositionControl]),
  target: 'map',
  layers: [
    new TileLayer({
      source: DAUM_ROAD_MAP,
    })
  ],
  view: new View({
    center: [195063,442898],
    resolution: 16
  }),
});
