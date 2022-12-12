//source : https://yd-dev.tistory.com/2
import './style.css'

import {Deck} from '@deck.gl/core';
import {BitmapLayer} from '@deck.gl/layers';
import {TileLayer} from '@deck.gl/geo-layers';

import {load} from '@loaders.gl/core';

const INITIAL_VIEW_STATE = {
  latitude: 37.4,
  longitude: 127.11,
  zoom: 12
};

const deckgl = new Deck({
  initialViewState: INITIAL_VIEW_STATE,
  controller: true,
  layers: [
    new TileLayer({
      minZoom: 0,
      maxZoom: 19,
      tileSize: 256,

      getTileData: ({index}) => {
        const {x, y, z} = index;
        return load('https://f.basemaps.cartocdn.com/dark_nolabels/' + z + '/' + x + '/' + y + '.png')
      },

      renderSubLayers: props => {
        const {
          bbox: {west, south, east, north}
        } = props.tile;

        return new BitmapLayer(props, {
          data: null,
          image: props.data,
          bounds: [west, south, east, north]
        });
      },
    })
  ]
});
