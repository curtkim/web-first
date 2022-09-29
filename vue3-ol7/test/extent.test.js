import { describe, expect, it } from 'vitest'
import {Point, Polygon} from 'ol/geom';
import {createEmpty, extend} from 'ol/Extent';

describe('extent', ()=>{
  it('getExtent', ()=>{
    const polygon = new Polygon([[[0, 0], [200000, 0],[100000, 100000],[0, 0]]])
    const point = new Point([-1, -1]);

    let extent = createEmpty()
    polygon.getExtent(extent)
    expect(extent).toStrictEqual([ 0, 0, 200000, 100000 ])

    let extent2 = createEmpty()
    point.getExtent(extent2)
    expect(extent2).toStrictEqual([ -1, -1, -1, -1])

    let all = createEmpty()
    extend(all, extent)
    extend(all, extent2)
    expect(all).toStrictEqual([ -1, -1, 200000, 100000])
  })
})