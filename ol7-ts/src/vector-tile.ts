import './style.css'
import 'ol/ol.css'

//import WebGLTileLayerRenderer from 'ol/renderer/webgl/TileLayer';
import VectorTileLayer from 'ol/layer/VectorTile';
import VectorTileSource from 'ol/source/VectorTile';
import {Map, View} from 'ol/index';
import {applyBackground, applyStyle} from 'ol-mapbox-style';
import {createXYZ} from 'ol/tilegrid';

const key = 'uFt1gMmkqbAsLgYWRdO2';
const url = 'https://api.maptiler.com/maps/basic-4326/style.json?key=' + key;

// Match the server resolutions
const tileGrid = createXYZ({
  extent: [-180, -90, 180, 90],
  tileSize: 512,
  maxResolution: 180 / 512,
  maxZoom: 13,
});

const layer = new VectorTileLayer({
  declutter: true,
  source: new VectorTileSource({
    projection: 'EPSG:4326',
    tileGrid: tileGrid,
  }),
});
applyStyle(layer, url, '', {resolutions: tileGrid.getResolutions()});
applyBackground(layer, url);

const map = new Map({
  target: 'map',
  layers: [layer],
  view: new View({
    projection: 'EPSG:4326',
    zoom: 0,
    center: [0, 30],
  }),
});