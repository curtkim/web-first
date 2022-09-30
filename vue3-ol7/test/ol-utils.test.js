import { describe, expect, it } from 'vitest'
import {Point, Polygon} from 'ol/geom';
import { Feature } from 'ol';
import {makeAllExtent } from '../src/ol-utils.js'


describe('ol-utils', ()=>{
  it('makeAllExtent', ()=>{
    const features = [
      new Feature({
        geometry: new Polygon([[[0, 0], [200000, 0],[100000, 100000],[0, 0]]]),
      }),
      new Feature({
        geometry: new Point([-1, -1])
      })
    ]

    expect(makeAllExtent(features)).toStrictEqual(
      [ -1, -1, 200000, 100000]
    )
  })
})