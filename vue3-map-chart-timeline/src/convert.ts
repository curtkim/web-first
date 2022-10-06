import {LineString} from "ol/geom";
import {ComplexPoint} from "./models";

export function sample2line(sample: Array<ComplexPoint>) : LineString{
  return new LineString(sample.map(it=> [it.x, it.y]))
}