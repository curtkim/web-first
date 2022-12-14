import './style.css'

import {Deck, OrthographicView, MapView} from '@deck.gl/core';
import {BitmapLayer} from '@deck.gl/layers';
import {TileLayer} from '@deck.gl/geo-layers';
import {load} from '@loaders.gl/core';
import {_Tileset2D as Tileset2D} from '@deck.gl/geo-layers';

// map.daumcdn.net에
// access-control-allow-origin: *
// 없는 관계로 vite.config.js에 proxy를 설정함.
//
// 아직 작업중
// y inverse
// daum tile extent
// custom Tileset2D?

const falseOrigin = [-30000, -60000]

const maxExtent = [
  falseOrigin[0]-Math.pow(2,19)*2, falseOrigin[0]-Math.pow(2,19),
  falseOrigin[1]+Math.pow(2,19)*3, falseOrigin[1]+Math.pow(2, 19)*3
]; // minx miny maxx maxy

const tilePixelRatio = window.devicePixelRatio > 1 ? 2 : 1;
const TILE_SIZE = 256;

class DaumTileset2D extends Tileset2D {
  getTileIndices({viewport, maxZoom, minZoom, zRange, modelMatrix, modelMatrixInverse}) {
    console.log(maxZoom, minZoom, viewport, viewport.getBounds(), modelMatrix, modelMatrixInverse)

    // viewport
    // cameraPosition [195063, 442898, 1024]
    // center [195063, 442898, 0]
    // x 0
    // y 0
    // zoom -10
    // width 1960
    // height 800
    // ...
    const z = Math.round(-1 * viewport.zoom);
    const unitSize = TILE_SIZE * Math.pow(2, z-3);
    const [minX, minY, maxX, maxY] = viewport.getBounds();

    const tileMinX = Math.floor((minX - falseOrigin[0]) / unitSize);
    const tileMinY = Math.floor((minY - falseOrigin[1]) / unitSize);
    const tileMaxX = Math.ceil((maxX - falseOrigin[0]) / unitSize);
    const tileMaxY = Math.ceil((maxY - falseOrigin[1]) / unitSize);

    /*
    let indices = []
    for (let x = tileMinX; x < tileMaxX; x++) {
      for (let y = tileMinY; y < tileMaxY; y++) {
        indices.push({x, y, z: -1*z});
      }
    }
    */
    console.log(unitSize, z, 'minx,miny,maxx,maxy', tileMinX, tileMinY, tileMaxX, tileMaxY)

    const results = super.getTileIndices({viewport, maxZoom, minZoom, zRange, modelMatrix, modelMatrixInverse});
    console.log(results);
    return results;
    //return super.getTileIndices({viewport, maxZoom, minZoom, zRange, modelMatrix, modelMatrixInverse});
    // Quadkeys and OSM tiles share the layout, leverage existing algorithm
    // Data format: [{quadkey: '0120'}, {quadkey: '0121'}, {quadkey: '0120'},...]
    /*
    const bounds = viewport.getBounds();

    for (let x = Math.floor(minX); x < maxX; x++) {
      for (let y = Math.floor(minY); y < maxY; y++) {
        indices.push({x, y, z});
      }
    }
    return indices;
    return super.getTileIndices(opts).map(tileToQuadkey);
    */
  }

  getTileId(index) {
    return super.getTileId(index);
    //return `${index.z}/${index.y}/${index.z}`;
  }

  getTileZoom(index) {
    return index.z;
  }

  getParentIndex(index) {
    //return super.getParentIndex(index);

    const x = Math.floor(index.x / 2);
    const y = Math.floor(index.y / 2);
    const z = index.z - 1;
    return {
      x,
      y,
      z
    };
    /*
    const {x,y,z} = index;
    const unitSize = Math.pow(2, z-3);
    const mapX = falseOrigin[0] + x*unitSize;
    const mapY = falseOrigin[1] + y*unitSize;

    const parentUnitSize = unitSize*2;
    const parentTileX = (mapX - falseOrigin[0])/parentUnitSize;
    const parentTileY = (mapY - falseOrigin[1])/parentUnitSize;
    return {x:parentTileX, y:parentTileY, z:z-1};
    */
  }
}

const INITIAL_VIEW_STATE = {
  target: [195063,442898, 0],
  zoom: -10
};

const deckgl = new Deck({
  initialViewState: INITIAL_VIEW_STATE,
  views: new OrthographicView({id: 'ortho'}),
  controller: true,
  layers: [
    new TileLayer({
      TilesetClass: DaumTileset2D,

      //data: 'http://map0.daumcdn.net/map_2d_hd/2212ejo/L{z}/{y}/{x}.png',

      getTileData: ({index}) => {
        //console.log(index)
        const {x, y, z} = index;
        var Y = -y - 1;

        var idx = x % 4;
        if (idx < 0) idx = idx + 4;
        const map_type = "map_2d_hd";
        const tileVersion = "2212ejo"
        const url = '/' + map_type + '/' + tileVersion + '/L' + (-1*z) + '/' + Y + '/' + x + '.png';

        //return load(url)
        return url;
      },

      maxZoom: -1,
      minZoom: -14,
      tileSize: TILE_SIZE,

      renderSubLayers: props => {
        //console.log(props.tile.index.z, props)
        const {
          bbox: {left, bottom, right, top}
        } = props.tile;

        return new BitmapLayer(props, {
          data: null,
          image: props.data,
          bounds: [left, bottom, right, top]
        });
      }
    })
  ],
  onViewStateChange: ({viewState}) => {
    viewState.zoom = Math.round(viewState.zoom)
    return viewState;
  }
});
