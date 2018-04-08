var fs = require('fs');
var path = require('path');
var webpack = require('webpack');


var DIRS = [
  '01-hot-loader',
  '02-css-loader',
  '03-bootstrap',
  '04-table',
  '05-chart',
  '06-map',
  '07-coffee',
]
entry = {}
DIRS.forEach(function(dir){
  entry[dir] = [
    'webpack-dev-server/client?http://localhost:3000',
    'webpack/hot/only-dev-server',
    './public/' + dir + '/index'
  ]
})

module.exports = {
  devtool: 'eval',
  entry: entry,
  output: {
    path: path.join(__dirname, 'dist'),
    filename: '[name].js',
    publicPath: '/dist/'
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin()
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        loaders: ['react-hot', 'babel'],
        include: path.join(__dirname, 'public')
      },
      {
        test: /\.css$/,
        loaders: ['style', 'css']
      },
      {
        test: /\.(eot|ttf|svg|gif|png)$/,
        loader: "file-loader"
      },
      {
        test: /\.woff$/,
        loader: "url-loader?limit=10000&mimetype=application/font-woff&name=[path][name].[ext]"
      },
      {
        test: /\.woff2$/,
        loader: "url-loader?limit=10000&mimetype=application/font-woff2&name=[path][name].[ext]"
      },
      {
        test: /\.coffee$/,
        loader: 'coffee'
      },
    ]
  },
  resolve: {
    extensions: ['', '.js', '.css', '.coffee'],
    root: [path.join(__dirname, './public')]
  }
};