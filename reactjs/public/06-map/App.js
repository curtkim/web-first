import React, { Component } from 'react';
import MyMap from './mymap';

import 'bootstrap/dist/css/bootstrap.css'
import './app.css'

var App = React.createClass({
  getInitialState: function () {
    return { cube: [], regions:[] }
  },

  componentDidMount: function() {
    console.log('componentDidMount')
    var _this = this
    $.getJSON('/public/data1.json', function(data){
      _this.setState({cube:data, regions:_this.state.regions})
    })
    $.getJSON('/public/region3.json', function(data){
      _this.setState({cube:_this.state.cube, regions:data})
    })
  },

  loadAnother: function(){
    var _this = this
    $.getJSON('/public/data2.json', function(data){
      _this.setState({cube:data, regions:_this.state.regions})
      //_this._grid.forceUpdate()
    })
  },

  render: function() {
    return (
      <div className="container-fluid">
        <div className="row">
          <div className="col-sm-12 content">
            <div className="page-header">
              <h3>시간대별 배차 현황</h3>
            </div>

            <MyMap width={1000} height={500} className={'map'} cube={this.state.cube} regions={this.state.regions}>
            </MyMap>

            <button onClick={this.loadAnother}>change</button>
          </div>
        </div>
      </div>
    );
  }
})

export default App