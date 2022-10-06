
/*
const feature = new Feature({
  geometry: new Polygon([[[0, 0], [200000, 0],[100000, 100000],[0, 0]]]),
  value: 10,
  name: 'My Polygon',
  color: '#00FF00',
});
const features = ref([feature])
*/

/*
const style = new Style({
  fill: new Fill({
    color: '#ee0000',
  }),
});
const styleFunction = function (feature) {
  const color = feature.get('color') || '#ff0000'
  style.getFill().setColor(color);
  return style;
}
*/

import {Fill, RegularShape, Stroke, Style} from "ol/style";

const stroke = new Stroke({color: 'black', width: 2});
const fill = new Fill({color: 'red'});
export const triangle = new Style({
  image: new RegularShape({
    fill: fill,
    stroke: stroke,
    points: 3,
    radius: 10,
    rotation: 0, //Math.PI / 4,
    angle: 0,
  }),
})
export const lineStyle = new Style({
  stroke: new Stroke({color: 'red', width:3})
})
