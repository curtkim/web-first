import {Point, Polygon} from 'ol/geom';
import {createEmpty, extend} from 'ol/Extent';

export function makeAllExtent(features){
    let all = createEmpty()
    features.forEach(f => {
      let extent = createEmpty()
      f.getGeometry().getExtent(extent)
      extend(all, extent) 
    });
    return all;
}