const HtmlWebpackPlugin = require('html-webpack-plugin');

const CONFIG = {
  mode: 'development',

  entry: {
    app: './app.js'
  }

  //plugins: [new HtmlWebpackPlugin({title: 'deck.gl example'})]
};

// This line enables bundling against src in this repo rather than installed module
module.exports = CONFIG;
