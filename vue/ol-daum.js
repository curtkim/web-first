function is_high_resolution_screen() {
  return window.devicePixelRatio > 1;
}

ol.source.DaumImg = function(opt_options) {
  var options = opt_options || {};
  if( !options.tileVersion) options.tileVersion = '4bauto'
  var resolutions = [];
  for(var i = 14; i >= 1; i--)
    resolutions.push(Math.pow(2, i-3))
  var maxExtent = [-30000-Math.pow(2,19)*2, -60000-Math.pow(2,19), -30000+Math.pow(2,19)*3, -60000+Math.pow(2, 19)*3]; // minx miny maxx maxy
  var tilePixelRatio = options.tilePixelRatio || is_high_resolution_screen() ? 2 : 1;
  ol.source.TileImage.call(this, {
    tilePixelRatio: tilePixelRatio,
    tileGrid: new ol.tilegrid.TileGrid({
      extent: maxExtent,
      origin: [-30000, -60000],
      resolutions: resolutions
    }),
    tileUrlFunction: function(coordinate) {
      if (coordinate === null) return undefined;
      var z = 14-coordinate[0];
      var x = coordinate[1];
      var y = coordinate[2];
      //console.log(coordinate, z);
      var idx = x % 4;
      if(idx < 0 ) idx = idx+4;
      var map_type = tilePixelRatio >=2 ? 'map_2d_hd' : 'map_2d';
      var url = 'http://map' + idx +'.daumcdn.net/' + map_type + '/'+ options.tileVersion +'/L'+z+'/'+ y +'/'+x +'.png';
      return url;
    }
  });
};
ol.inherits(ol.source.DaumImg, ol.source.TileImage);