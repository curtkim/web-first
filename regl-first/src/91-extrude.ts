// https://github.com/pissang/geometry-extrude-example-regl/blob/gh-pages/src/main.js
import REGL from 'regl'
const regl = REGL()
import {lookAt, perspective, identity} from 'gl-mat4'
import {extrudePolygon} from 'geometry-extrude';

const squareWithHole = [
  [[0, 0], [10, 0], [10, 10], [0, 10]],
  // Hole
  [[2, 2], [8, 2], [8, 8], [2, 8]]
];
const {indices, position, uv, normal} = extrudePolygon([squareWithHole], {
  depth: 5
});

console.log(position)
console.log(identity([]))
console.log(lookAt([], [0, 0, 100], [0, 0, 0], [0, 0, 1]))

// @ts-ignore
const draw = regl({
  frag: `
  precision mediump float;
  varying vec3 vNormal;
  void main() {
      gl_FragColor = vec4(vNormal, 1.0);
  }
  `,

  vert: `
  precision mediump float;
  attribute vec3 position;
  attribute vec3 normal;
  uniform mat4 projection, view, model;
  varying vec3 vNormal;
  void main() {
      gl_Position = projection * view * model * vec4(position, 1.0);
      vNormal = normal;
  } 
  `,
  uniforms: {
    projection: ({viewportWidth, viewportHeight}) => perspective([], Math.PI / 4, viewportWidth / viewportHeight, 0.01, 1000),
    model: (_, props, batchId) => identity([]),
    view: () => lookAt([], [5, 5, 30], [0, 0, 0], [0, 0, 1])
  },
  attributes: {
    position: position,
    uv: uv,
    normal: normal
  },

  elements: indices
});

regl.frame(_ => {
    draw();
});