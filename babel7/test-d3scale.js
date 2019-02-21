import {scaleLinear} from 'd3-scale';

console.log(scaleLinear)
const ramp= scaleLinear([0,10],["blue","red"])
console.log(ramp(5))