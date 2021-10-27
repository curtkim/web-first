import React, {Component} from 'react';
import {render} from 'react-dom';
import {StaticMap} from 'react-map-gl';
import DeckGL, {COORDINATE_SYSTEM, SimpleMeshLayer, OrbitView} from 'deck.gl';
import {PointCloudLayer} from '@deck.gl/layers';
import {CubeGeometry} from '@luma.gl/core'

import {OBJLoader} from '@loaders.gl/obj';
import {registerLoaders} from '@loaders.gl/core';

// Add the loaders that handle your mesh format here
registerLoaders([OBJLoader]);


// Set your mapbox token here
const MAPBOX_TOKEN = process.env.MapboxAccessToken; // eslint-disable-line

const MESH_URL =
  'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/mesh/minicooper.obj';


const INITIAL_VIEW_STATE = {
  latitude: 37.39407492778153,
  longitude: 127.11011188242523,
  zoom: 13,
  bearing: 0,
  pitch: 30
};

class Root extends Component {
  _onClick(info) {
    if (info.object) {
      // eslint-disable-next-line
      alert(`${info.object.properties.name} (${info.object.properties.abbrev})`);
    }
  }

  render() {
    const layers = [
      new SimpleMeshLayer({
        id: 'mini-coopers',
        data: [
          {
            position: [37.39407492778153, 127.11011188242523],
            angle: 0,
            color: [255, 0, 0]
          }
        ],
        //mesh: MESH_URL,
        mesh: new CubeGeometry(),
        getPosition: d => d.position,
        getColor: d => d.color,
        getOrientation: d => [0, d.angle, 0]        
      })

    ];

    return (
      <DeckGL initialViewState={INITIAL_VIEW_STATE} controller={true} layers={layers}>
        <StaticMap mapboxApiAccessToken={MAPBOX_TOKEN} mapStyle="mapbox://styles/mapbox/light-v9" />
      </DeckGL>
    );
  }
}

/* global document */
render(<Root />, document.body.appendChild(document.createElement('div')));