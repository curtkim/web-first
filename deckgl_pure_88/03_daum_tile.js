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



const INITIAL_VIEW_STATE = {
  target: [195063,442898, 0],
  zoom: -7
};

const deckgl = new Deck({
  initialViewState: INITIAL_VIEW_STATE,
  views: new OrthographicView({id: 'ortho'}),
  controller: true,
  layers: [
    new TileLayer({
      //data: 'http://map0.daumcdn.net/map_2d_hd/2212ejo/L{z}/{y}/{x}.png',

      getTileData: ({index}) => {
        console.log(index)
        const {x, y, z} = index;

        var idx = x % 4;
        if (idx < 0) idx = idx + 4;
        const map_type = "map_2d_hd";
        const tileVersion = "2212ejo"
        const url = '/' + map_type + '/' + tileVersion + '/L'
          + (-1*z) + '/' + y + '/' + x + '.png';

        //return load(url)
        return url;
      },

      maxZoom: -1,
      minZoom: -14,
      tileSize: 512,

      renderSubLayers: props => {
        console.log(props)
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
  ]
});
