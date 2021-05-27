/* eslint-disable no-unused-vars */
import React, {PureComponent} from 'react';
import {render} from 'react-dom';
import DeckGL, {COORDINATE_SYSTEM, PointCloudLayer, OrbitView, LinearInterpolator} from 'deck.gl';


import {PCDWorkerLoader} from '@loaders.gl/pcd';
import {registerLoaders} from '@loaders.gl/core';

// Additional format support can be added here, see
// https://github.com/uber-web/loaders.gl/blob/master/docs/api-reference/core/register-loaders.md
registerLoaders(PCDWorkerLoader);
// registerLoaders(PLYWorkerLoader);

//const PCD_SAMPLE = 'test_ascii.pcd'
const PCD_SAMPLE = 'result.pcd'

const INITIAL_VIEW_STATE = {
  target: [0, 0, 0],//[210179, 433587, 0],
  rotationX: 0,
  rotationOrbit: -90,
  //orbitAxis: 'X',
  fov: 45,
  minZoom: 0,
  maxZoom: 10,
  zoom: 7
};

export class App extends PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      viewState: INITIAL_VIEW_STATE
    };

    this._onLoad = this._onLoad.bind(this);
    this._onViewStateChange = this._onViewStateChange.bind(this);
  }

  _onViewStateChange({viewState}) {
    this.setState({viewState});
  }

  _onLoad({header, loaderData, progress}) {
    // metadata from LAZ file header
    console.log(loaderData.header)
    console.log(loaderData.attributes)
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
        id: 'pcd-point-cloud-layer',
        data: PCD_SAMPLE,
        onDataLoad: this._onLoad,
        coordinateSystem: COORDINATE_SYSTEM.IDENTITY,
        getNormal: [0, 1, 0],
        //getColor: [255, 255, 255],
        opacity: 1,
        pointSize: 3
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