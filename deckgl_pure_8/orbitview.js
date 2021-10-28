import {Deck, OrbitView} from '@deck.gl/core';
import {SimpleMeshLayer} from '@deck.gl/mesh-layers';
import {PLYLoader} from '@loaders.gl/ply';


export const deck = new Deck({
  views: new OrbitView({
    orbitAxis: 'Y',
    // fovy: 50,
    // near: 0.1,
    // far: 1000,
    // orthographic: false
  }),
  initialViewState: {
    target: [0, 0, 0],
    zoom: -2,
    rotationOrbit: 180,
    rotationX: 0,
    minRotationX: -90,
    maxRotationX: 90,
    minZoom: -10,
    maxZoom: 10
  },
  controller: true,
  
  layers: [
    new SimpleMeshLayer({
      data: [0],
      mesh: 'https://raw.githubusercontent.com/visgl/deck.gl-data/master/examples/point-cloud-ply/lucy100k.ply',
      getPosition: [0, 0, 0],
      getColor: [200, 200, 200],
      loaders: [PLYLoader]
    })
  ]
});
  
