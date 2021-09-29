import React, {PureComponent} from 'react';
import {render} from 'react-dom';
import DeckGL, {COORDINATE_SYSTEM, SimpleMeshLayer, OrbitView} from 'deck.gl';
import {OBJLoader} from '@loaders.gl/obj';
import {registerLoaders} from '@loaders.gl/core';
import {CubeGeometry} from '@luma.gl/core'

// Add the loaders that handle your mesh format here
registerLoaders([OBJLoader]);

const INITIAL_VIEW_STATE = {
  longitude: -122.4,
  latitude: 37.74,
  zoom: 11,
  maxZoom: 20,
  pitch: 30,
  bearing: 0
};


class Example extends PureComponent {
  render() {
    const layers = [
      new SimpleMeshLayer({
        id: 'SimpleMeshLayer',
        data: 'https://raw.githubusercontent.com/visgl/deck.gl-data/master/website/bart-stations.json',
        
        /* props from SimpleMeshLayer class */
        
        getColor: d => [Math.sqrt(d.exits), 140, 0],
        getOrientation: d => [0, Math.random() * 180, 0],
        getPosition: d => d.coordinates,
        // getScale: [1, 1, 1],
        // getTransformMatrix: [],
        // getTranslation: [0, 0, 0],
        // material: true,
        //mesh: 'https://raw.githubusercontent.com/visgl/deck.gl-data/master/website/humanoid_quad.obj',
        mesh: new CubeGeometry(),
        sizeScale: 300,
        // texture: null,
        // wireframe: false,
        
        /* props inherited from Layer class */
        
        // autoHighlight: false,
        // coordinateOrigin: [0, 0, 0],
        // coordinateSystem: COORDINATE_SYSTEM.LNGLAT,
        // highlightColor: [0, 0, 128, 128],
        loaders: [OBJLoader],
        // modelMatrix: null,
        // opacity: 1,
        pickable: true,
        // visible: true,
        // wrapLongitude: false,
      })
    ];
    
    return (
      <DeckGL
        initialViewState={INITIAL_VIEW_STATE}
        controller={true}
        layers={layers}
      >
      </DeckGL>
    );

  }
}

/*
export function renderToDOM(container) {
  render(<Example />, container);
}
*/

/* global document */
render(<Example />, document.getElementById('app'));

console.log("done");