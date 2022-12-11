import './style.css'

import {Deck, OrthographicView} from '@deck.gl/core';
import {BitmapLayer} from '@deck.gl/layers';
import {TileLayer} from '@deck.gl/geo-layers';

const INITIAL_VIEW_STATE = {
  target: [0, 0, 0],
  zoom: 1
};

const deckgl = new Deck({
  initialViewState: INITIAL_VIEW_STATE,
  views: new OrthographicView(),
  controller: true,
  layers: [
    new TileLayer({
      // https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Tile_servers
      data: 'https://c.tile.openstreetmap.org/{z}/{x}/{y}.png',

      minZoom: 0,
      maxZoom: 19,
      tileSize: 256,

      renderSubLayers: props => {
        console.log(props)
        const {
          bbox: {west, south, east, north}
        } = props.tile;

        return new BitmapLayer(props, {
          data: null,
          image: props.data,
          bounds: [west, south, east, north]
        });
      }
    })
  ]
});
