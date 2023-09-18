import TileImage from 'ol/source/TileImage';
import TileGrid from 'ol/tilegrid/TileGrid'

var resolutions = [];
for(var i = 14; i >= 1; i--)
  resolutions.push(Math.pow(2, i-3))
//console.log(resolutions);

const maxExtent = [
  -30000-Math.pow(2,19)*2, -60000-Math.pow(2,19), 
  -30000+Math.pow(2,19)*3, -60000+Math.pow(2, 19)*3
]; // minx miny maxx maxy

var tilePixelRatio = window.devicePixelRatio > 1 ? 2 : 1;

export const TILE_GRID = new TileGrid({
  extent: maxExtent,
  origin: [-30000, -60000],
  resolutions: resolutions
})

export const DAUM_ROAD_MAP = new TileImage({
  tileGrid: TILE_GRID,
  tilePixelRatio: tilePixelRatio,
  tileUrlFunction: function (coordinate) {
    if (coordinate === null) return undefined;
    var z = 14 - coordinate[0];
    var x = coordinate[1];
    var y = -coordinate[2] - 1;

    // @ts-ignore
    var uriFunc = kakao.maps.URI_FUNC[tilePixelRatio >= 2 ? 'ROADMAP_HD' : 'ROADMAP'];
    return 'https://' + uriFunc(x, y, z);
  }
});