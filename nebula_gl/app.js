import {Deck} from '@deck.gl/core';
import {COORDINATE_SYSTEM, OrbitView, LinearInterpolator} from '@deck.gl/core';
import {PointCloudLayer} from '@deck.gl/layers';

import {LASWorkerLoader} from '@loaders.gl/las';
import {PCDWorkerLoader} from '@loaders.gl/pcd';
import {registerLoaders} from '@loaders.gl/core';

// Additional format support can be added here, see
// https://github.com/uber-web/loaders.gl/blob/master/docs/api-reference/core/register-loaders.md
registerLoaders(LASWorkerLoader);
registerLoaders(PCDWorkerLoader);

const LAZ_SAMPLE =
  'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-laz/indoor.0.1.laz';
const PCD_SAMPLE = '1593754930744973.pcd'


const INITIAL_VIEW_STATE = {
  target: [0, 0, 0],
  rotationX: 0,
  rotationOrbit: 0,
  orbitAxis: 'Y',
  fov: 50,
  minZoom: 0,
  maxZoom: 10,
  zoom: 1
};

export const deck = new Deck({
  canvas: 'deck-canvas',
  width: '100%',
  height: '100%',
  initialViewState: INITIAL_VIEW_STATE,
  views: new OrbitView(),
  controller: true,
  onViewStateChange: ({viewState}) => {
    //this.setState({viewState});
  },
  parameters: {
    clearColor: [0.93, 0.86, 0.81, 1]
  },  
  layers: [
    new PointCloudLayer({
      id: 'point-cloud-layer',
      data: LAZ_SAMPLE,
      //onDataLoad: this._onLoad,
      coordinateSystem: COORDINATE_SYSTEM.IDENTITY,
      getNormal: [0, 1, 0],
      getColor: [255, 255, 255],
      opacity: 0.5,
      pointSize: 0.5
    })
  ]
});