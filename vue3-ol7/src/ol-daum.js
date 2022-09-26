import TileImage from 'ol/source/TileImage';
import TileGrid from 'ol/tilegrid/TileGrid'

var resolutions = [];
for(var i = 14; i >= 1; i--)
  resolutions.push(Math.pow(2, i-3))

var maxExtent = [
	  -30000-Math.pow(2,19)*2, -60000-Math.pow(2,19), 
	  -30000+Math.pow(2,19)*3, -60000+Math.pow(2, 19)*3
]; // minx miny maxx maxy
var tilePixelRatio = window.devicePixelRatio > 1 ? 2 : 1;

var DaumRoadMap = new TileImage({
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

		//console.log(coordinate, z,y,x);
		var idx = x % 4;
		if(idx < 0 ) idx = idx+4;
		var map_type = tilePixelRatio >=2 ? 'map_2d_hd' : 'map_2d';
		var tileVersion = kakao.maps.VERSION.ROADMAP;//'2205pfk'
		var url = 'http://map' + idx +'.daumcdn.net/' + map_type + '/'+ tileVersion +'/L'+z+'/'+ y +'/'+x +'.png';
		return url;
	}
})

export default {DaumRoadMap}

