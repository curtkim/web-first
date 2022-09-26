import './style.css';
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import TileImage from 'ol/source/TileImage';
import TileGrid from 'ol/tilegrid/TileGrid'
import MousePosition from 'ol/control/MousePosition';
import {createStringXY} from 'ol/coordinate';
import {defaults as defaultControls} from 'ol/control';

const mousePositionControl = new MousePosition({
  coordinateFormat: createStringXY(4)
});


var resolutions = [];
for(var i = 14; i >= 1; i--)
  resolutions.push(Math.pow(2, i-3))
console.log(resolutions);

var maxExtent = [
  -30000-Math.pow(2,19)*2, -60000-Math.pow(2,19), 
  -30000+Math.pow(2,19)*3, -60000+Math.pow(2, 19)*3
]; // minx miny maxx maxy
var tilePixelRatio = window.devicePixelRatio > 1 ? 2 : 1;


const map = new Map({
  controls: defaultControls().extend([ mousePositionControl]),
  target: 'map',
  layers: [
    new TileLayer({
      source: new TileImage({
        tileGrid: new TileGrid({
          extent: maxExtent,
          origin: [-30000, -60000],
          resolutions: resolutions
        }),
        tilePixelRatio: tilePixelRatio,
        tileUrlFunction: function(coordinate) {
          if (coordinate === null) return undefined;
          var z = 14-coordinate[0];
          var x = coordinate[1];
          var y = -coordinate[2]-1;

          console.log(coordinate, z,y,x);
          var idx = x % 4;
          if(idx < 0 ) idx = idx+4;
          var map_type = tilePixelRatio >=2 ? 'map_2d_hd' : 'map_2d';
          var tileVersion = kakao.maps.VERSION.ROADMAP;//'2205pfk'
          var url = 'http://map' + idx +'.daumcdn.net/' + map_type + '/'+ tileVersion +'/L'+z+'/'+ y +'/'+x +'.png';
          return url;
        }
      }),
    }),
  ],
  view: new View({
    center: [195063,442898],
    resolution: 16
  }),
});

