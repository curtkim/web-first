import React, {Component} from 'react';
import {render} from 'react-dom';
import {StaticMap} from 'react-map-gl';
import DeckGL, {COORDINATE_SYSTEM, SimpleMeshLayer, OrbitView} from 'deck.gl';
import {PointCloudLayer} from '@deck.gl/layers';


// Set your mapbox token here
const MAPBOX_TOKEN = process.env.MapboxAccessToken; // eslint-disable-line

const URL = 'pointcloud.json'

const INITIAL_VIEW_STATE = {
  latitude: 37.74,
  longitude: -122.4,
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
        new PointCloudLayer({
            id: 'point-cloud-layer',
            data: URL,
            pickable: false,
            coordinateSystem: COORDINATE_SYSTEM.METER_OFFSETS,
            coordinateOrigin: [-122.4, 37.74],
            radiusPixels: 4,
            getPosition: d => d.position,
            getNormal: d => d.normal,
            getColor: d => d.color,
            onHover: ({object, x, y}) => {
              const tooltip = object.position.join(', ');
            }
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