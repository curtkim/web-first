import {Deck} from '@deck.gl/core';
import {PolygonLayer} from '@deck.gl/layers';
import Map from './mapbox';

const DATA = 'sf-zipcodes.json'; //eslint-disable-line
const MAPBOX_TOKEN = process.env.MapboxAccessToken; // eslint-disable-line

const INITIAL_VIEW_STATE = {
  latitude: 37.78346622,
  longitude: -122.42177834,
  zoom: 11.5,
  bearing: 0,
  pitch: 60
};

const map = new Map({
  mapboxApiAccessToken: MAPBOX_TOKEN,
  container: 'map',
  style: 'mapbox://styles/mapbox/light-v9',
  viewState: INITIAL_VIEW_STATE
});

function newLayer(extruded, getElevation){
  return new PolygonLayer({
    id: 'polygon-layer',
    data: DATA,
    extruded,
    pickable: true,
    stroked: true,
    filled: true,
    wireframe: true,
    lineWidthMinPixels: 1,
    getPolygon: d => d.contour,
    getElevation: getElevation,
    getFillColor: d => [d.population / d.area / 60, 140, 0],
    getLineColor: [80, 80, 80],
    getLineWidth: 1,

    // TODO setTooltip에서 에러가 발생한다.
    onHover: ({x, y, object}) => {
      //console.log(x, y, object)
      //if(object)
      //  console.log(`${object.zipcode}\nPopulation: ${object.population}`)
      //setTooltip(`${object.zipcode}\nPopulation: ${object.population}`)
    },
    onClick: (e) => {
      const object = e.object
      const el = document.getElementById('popup')
      el.innerHTML = `${object.zipcode}\nPopulation: ${object.population}`
      el.style.top = e.y+"px"
      el.style.left = e.x+"px"
    }
  })

}

function getElevation1(d){
  return d.population / d.area / 10;
}
function getElevation2(d){
  return d.population / 10;
}

const layer = newLayer(true, getElevation1)

export const deck = new Deck({
  canvas: 'deck-canvas',
  width: '100%',
  height: '100%',
  initialViewState: INITIAL_VIEW_STATE,
  controller: true,
  onViewStateChange: ({viewState}) => {
    //console.log(viewState)
    map.setProps({viewState});
  },
  layers: [ layer ]
});


let a = true

document.getElementById('chkExtrude').onchange = function(){
  deck.setProps({
    layers: [
      newLayer(
        document.getElementById('chkExtrude').checked,
        a ? getElevation1 : getElevation2
      )
    ]
  })
}



document.getElementById('chkElevation').onchange = function(){
  a = !a
  console.log('a', a)

  deck.setProps({
    layers: [
      newLayer(
        !document.getElementById('chkExtrude').checked,
        a ? getElevation1 : getElevation2
      )
    ]
  })

  deck.setProps({
    layers: [
      newLayer(
        document.getElementById('chkExtrude').checked,
        a ? getElevation1 : getElevation2
      )
    ]
  })
}
