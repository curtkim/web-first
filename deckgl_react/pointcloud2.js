/* eslint-disable no-unused-vars */
import React, {PureComponent} from 'react';
import {render} from 'react-dom';
import DeckGL, {COORDINATE_SYSTEM, PointCloudLayer, OrbitView, LinearInterpolator} from 'deck.gl';

import {LASWorkerLoader} from '@loaders.gl/las';
import {registerLoaders} from '@loaders.gl/core';

// Additional format support can be added here, see
// https://github.com/uber-web/loaders.gl/blob/master/docs/api-reference/core/register-loaders.md
registerLoaders(LASWorkerLoader);
// registerLoaders(PLYWorkerLoader);

// Data source: kaarta.com
const LAZ_SAMPLE =
  'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-laz/indoor.0.1.laz';
// Data source: The Stanford 3D Scanning Repository
// const PLY_SAMPLE =
//   'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-ply/lucy800k.ply';

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

const transitionInterpolator = new LinearInterpolator(['rotationOrbit']);

export class App extends PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      viewState: INITIAL_VIEW_STATE
    };

    this._onLoad = this._onLoad.bind(this);
    this._onViewStateChange = this._onViewStateChange.bind(this);
    this._rotateCamera = this._rotateCamera.bind(this);
  }

  _onViewStateChange({viewState}) {
    this.setState({viewState});
  }

  _rotateCamera() {
    const {viewState} = this.state;
    this.setState({
      viewState: {
        ...viewState,
        rotationOrbit: viewState.rotationOrbit + 30,
        transitionDuration: 600,
        transitionInterpolator,
        onTransitionEnd: this._rotateCamera
      }
    });
  }

  _onLoad({header, loaderData, progress}) {
    // metadata from LAZ file header
    console.log(loaderData)
    const {mins, maxs} = loaderData.header;

    if (mins && maxs) {
      // File contains bounding box info
      this.setState(
        {
          viewState: {
            ...this.state.viewState,
            target: [(mins[0] + maxs[0]) / 2, (mins[1] + maxs[1]) / 2, (mins[2] + maxs[2]) / 2],
            /* global window */
            zoom: Math.log2(window.innerWidth / (maxs[0] - mins[0])) - 1
          }
        },
        this._rotateCamera
      );
    }

    if (this.props.onLoad) {
      this.props.onLoad({count: header.vertexCount, progress: 1});
    }
  }

  render() {
    const {viewState} = this.state;

    const layers = [
      new PointCloudLayer({
        id: 'laz-point-cloud-layer',
        data: LAZ_SAMPLE,
        onDataLoad: this._onLoad,
        coordinateSystem: COORDINATE_SYSTEM.IDENTITY,
        getNormal: [0, 1, 0],
        getColor: [255, 255, 255],
        opacity: 0.5,
        pointSize: 0.5
      })
    ];

    return (
      <DeckGL
        views={new OrbitView()}
        viewState={viewState}
        controller={true}
        onViewStateChange={this._onViewStateChange}
        layers={layers}
        parameters={{
          clearColor: [0.93, 0.86, 0.81, 1]
        }}
      />
    );
  }
}

/* global document */
render(<App />, document.body.appendChild(document.createElement('div')));