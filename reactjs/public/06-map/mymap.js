import React, { Component } from 'react';

var MyMap = React.createClass({

  _styleFun: function(feature, resolution){
    const {cube} = this.props
    let idx = 0
    if( !cube[idx])
      return null
    let value = cube[idx][feature.get('HCODE')][2]


    return new ol.style.Style({
      stroke: new ol.style.Stroke({
        color: value > 10 ? 'blue' : 'red',
        width: 1
      }),
      fill: new ol.style.Fill({
        color: value > 10 ? 'blue' : 'red'
        //color: 'rgba(0, 0, 255, 0.3)'
      })
    })
  },

  componentDidMount: function() {
    console.log('componentDidMount')
    proj4.defs("EPSG:5181","+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");

    var vector = new ol.layer.Vector({
      title: 'vector',
      source: new ol.source.Vector({url: '/public/korea-region-topo.json', format: new ol.format.TopoJSON({
        defaultDataProjection: ol.proj.get('EPSG:5181')
      })}),
      style: this._styleFun
    });

    var daumImgLayer = new ol.layer.Tile({
      source: new ol.source.DaumImg()
    })

    var map = new ol.Map({
      layers: [daumImgLayer, vector],
      target: this.refs.mapContainer,
      view: new ol.View({
        center: [195063,441898],
        zoom: 7,
        maxZoom: 14,
        maxResolution: 2048, // 14level(zoom=0)에서 1px은 2048m와 같다
        projection: ol.proj.get('EPSG:5181')
      }),
      controls: [
        new ol.control.Zoom(),
        new ol.control.MousePosition()
      ]
    })

    this._map = map
    this._vector = vector
  },

  componentDidUpdate: function () {
    console.log('componentDidUpdate and render')
    //console.log(this.props.cube)
    //console.log(this._map)
    //this._map.render()
    var source = this._vector.getSource()
    source.refresh()
    //var params = source.getParams()
    //params.t = new Date().getMilliseconds()
    //source.updateParams(params)
  },

  render: function() {
    const {className, width, height} = this.props
    const mapStyle = {
      width,
      height
    }

    let content = [
      <div key="map" ref="mapContainer"
        style={ mapStyle } className={ className }/>
    ]

    return (
      <div
        style={ {
          width: this.props.width,
          height: this.props.height,
          position: 'relative'
        } }>

        { content }
      </div>
    );
  }
})

export default MyMap