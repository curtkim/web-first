import {describe, it} from 'vitest'
import {expect} from "vitest";
import {MVT} from "ol/format";
import RenderFeature from "ol/render/Feature";
import {readFileSync} from 'fs'
import {Feature} from "ol";


const options = {
  featureProjection: 'EPSG:3857',
  extent: [
    1824704.739223726, 6141868.096770482, 1827150.7241288517, 6144314.081675608,
  ],
};

const data = readFileSync("test/14-8938-5680.vector.pbf")

describe('mvt_format.readFeatures', () => {
  it('uses ol.render.Feature as feature class by default', () => {
    const format = new MVT({layers: ['water']});
    const features = format.readFeatures(data, options);
    expect(features[0]).instanceOf(RenderFeature)
    expect(features[0].getProperties()).to.eql({
      osm_id: 0,
      layer: 'water'
    })
  })

  it('parses geometries correctly', function () {

    const format = new MVT({
      featureClass: Feature,
      layers: ['poi_label'],
    });
    let geometry;

    const features = format.readFeatures(data, options)
    //console.log(features[0])
    expect(features[0]).instanceOf(Feature)

    geometry = format.readFeatures(data)[0].getGeometry();
    expect(geometry.getType()).to.equal('Point')
    expect(geometry.getCoordinates()).to.eql([-1210, 2681])

    format.setLayers(['water']);
    geometry = format.readFeatures(data)[0].getGeometry();
    expect(geometry.getType()).to.equal('Polygon');
    expect(geometry.getCoordinates()[0].length).to.equal(10);
    expect(geometry.getCoordinates()[0][0]).to.eql([1007, 2302]);

    format.setLayers(['barrier_line']);
    geometry = format.readFeatures(data)[0].getGeometry();
    expect(geometry.getType()).to.equal('MultiLineString');
    expect(geometry.getCoordinates()[1].length).to.equal(6);
    expect(geometry.getCoordinates()[1][0]).to.eql([4160, 3489]);
  });

  it('avoids unnecessary reprojections of the ol.render.Feature', function () {
    const format = new MVT({
      layers: ['poi_label'],
    });
    const geometry = format.readFeatures(data)[0].getGeometry();
    expect(geometry.getType()).to.equal('Point');
    expect(geometry.getFlatCoordinates()).to.eql([-1210, 2681]);
  });

})