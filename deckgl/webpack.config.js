const { resolve } = require('path');
const webpack = require('webpack');

const CONFIG = {
    mode: 'development',

    entry: {
        arc: './arc.js',
        mesh: './mesh.js',
        pointcloud: './pointcloud.js',
        pointcloud2: './pointcloud2.js',
        pointcloud3: './pointcloud3.js',
        scenegraph: './scenegraph.js'
    },

    module: {
        rules: [
            {
                test: /\.js$/,
                loader: 'babel-loader',
                exclude: [/node_modules/],
                options: {
                    presets: ['@babel/preset-react']
                }
            }
        ]
    },

    resolve: {
        alias: {
            // From mapbox-gl-js README. Required for non-browserify bundlers (e.g. webpack):
            'mapbox-gl$': resolve('./node_modules/mapbox-gl/dist/mapbox-gl.js')
        }
    },

    // Optional: Enables reading mapbox token from environment variable
    plugins: [
        new webpack.EnvironmentPlugin(['MapboxAccessToken']),
    ],

    devServer: {
        port: 9000
    }
};

module.exports = CONFIG;
