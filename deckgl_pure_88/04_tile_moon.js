import './style.css'


import {Deck, OrthographicView, COORDINATE_SYSTEM} from '@deck.gl/core';
import {TileLayer} from '@deck.gl/geo-layers';
import {BitmapLayer} from '@deck.gl/layers';
import {load} from '@loaders.gl/core';
import {clamp} from '@math.gl/core';


const INITIAL_VIEW_STATE = {
  target: [13000, 13000, 0],
  zoom: -7
};

const ROOT_URL ='/moon.image';

function getTooltip({tile, bitmap}) {
  if (tile && bitmap) {
    const {x, y, z} = tile.index;
    return `\
    tile: x: ${x}, y: ${y}, z: ${z}
    (${bitmap.pixel[0]},${bitmap.pixel[1]}) in ${bitmap.size.width}x${bitmap.size.height}`;
  }
  return null;
}

const autoHighlight = true;
const TILE_SIZE = 512;
const WIDTH = 24000;
const HEIGHT = 24000;

const deckgl = new Deck({
  initialViewState: INITIAL_VIEW_STATE,
  views: new OrthographicView({id: 'ortho'}),
  controller: true,
  layers: [
    new TileLayer({
      pickable: autoHighlight,
      tileSize: TILE_SIZE,
      autoHighlight,
      highlightColor: [60, 60, 60, 100],
      minZoom: -7,
      maxZoom: 0,
      coordinateSystem: COORDINATE_SYSTEM.CARTESIAN,
      extent: [0, 0, WIDTH, HEIGHT],

      getTileData: ({index}) => {
        console.log(index)
        const {x, y, z} = index;
        return load(`${ROOT_URL}/moon.image_files/${15 + z}/${x}_${y}.jpeg`);
      },
      //onViewportLoad: onTilesLoad,

      renderSubLayers: props => {
        const {
          bbox: {left, bottom, right, top}
        } = props.tile;
        console.log(left, bottom, right, top)
        return new BitmapLayer(props, {
          data: null,
          image: props.data,
          bounds: [
            clamp(left, 0, WIDTH),
            clamp(bottom, 0, HEIGHT),
            clamp(right, 0, WIDTH),
            clamp(top, 0, HEIGHT)
          ]
        });
      }
    })
  ],
  getTooltip: getTooltip,
});
