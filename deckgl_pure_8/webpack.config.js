const CONFIG = {
  mode: 'development',

  entry: {
    arc: './arc.js',
    lane: './lane.js',
    lane_wgs: './lane_wgs.js',
    fps: './fps.js',
    orbitview: './orbitview.js'
  }

  //plugins: [new HtmlWebpackPlugin({title: 'deck.gl example'})]
};

// This line enables bundling against src in this repo rather than installed module
module.exports = CONFIG;
