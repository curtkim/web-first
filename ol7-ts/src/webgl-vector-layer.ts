import './style.css'
import 'ol/ol.css'

import GeoJSON from 'ol/format/GeoJSON';
import Layer from 'ol/layer/Layer';
import Map from 'ol/Map';
import OSM from 'ol/source/OSM';
import TileLayer from 'ol/layer/WebGLTile';
import VectorSource from 'ol/source/Vector';
import View from 'ol/View';
import WebGLVectorLayerRenderer from 'ol/renderer/webgl/VectorLayer';
import {asArray} from 'ol/color';
import {packColor} from 'ol/renderer/webgl/shaders';

class WebGLLayer extends Layer {
  createRenderer() {
    return new WebGLVectorLayerRenderer(this, {
      fill: {
        attributes: {
          color: function (feature) {
            const color = asArray(feature.get('COLOR') || '#eee');
            color[3] = 0.85;
            return packColor(color);
          },
          opacity: function () {
            return 0.6;
          },
        },
      },
      stroke: {
        attributes: {
          color: function (feature) {
            const color = [...asArray(feature.get('COLOR') || '#eee')];
            color.forEach((_, i) => (color[i] = Math.round(color[i] * 0.75))); // darken slightly
            return packColor(color);
          },
          width: function () {
            return 1.5;
          },
          opacity: function () {
            return 1;
          },
        },
      },
    });
  }
}

const osm = new TileLayer({
  source: new OSM(),
});

const vectorLayer = new WebGLLayer({
  source: new VectorSource({
    url: 'https://openlayers.org/data/vector/ecoregions.json',
    format: new GeoJSON(),
  }),
});

const map = new Map({
  layers: [osm, vectorLayer],
  target: 'map',
  view: new View({
    center: [0, 0],
    zoom: 1,
  }),
});