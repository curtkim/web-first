import React, { Component } from 'react';
import {Grid, AutoSizer} from 'react-virtualized';

import 'bootstrap/dist/css/bootstrap.css'
import 'react-virtualized/styles.css'
import './app.css'

var Demo = React.createClass({
  getInitialState: function () {
    return { count: 0 }
  },
  handleClick: function () {
    this.setState({
      count: this.state.count + 1,
    })
  },
  render: function () {
    return (
      <button onClick={this.handleClick}>
        Click me! Number of clicks: {this.state.count}
      </button>
    )
  }
})

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

  cellRenderer: function(params) {
    //console.log(params)
    //console.log(params) {columnIndex: 2, isScrolling: false, rowIndex: 27}

    var region = this.state.regions[params.rowIndex-1]
    if(params.rowIndex == 0 && params.columnIndex == 0)
      return ''
    else if(params.rowIndex == 0)
      return params.columnIndex-1 + '시'
    else if(params.columnIndex == 0)
      return region.name

    return this.state.cube[params.columnIndex-1][region.code][3]
  },

  _getColumnWidth: function({index}){
    switch(index){
      case 0: return 200
      default:
        return 50
    }
  },

  loadAnother: function(){
    var _this = this
    $.getJSON('/public/data2.json', function(data){
      _this.setState({cube:data, regions:_this.state.regions})
      _this._grid.forceUpdate()
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

            <Grid
              columnCount={this.state.cube.length}
              columnWidth={this._getColumnWidth}
              height={800}
              overscanRowCount={1}
              cellRenderer={this.cellRenderer}
              rowHeight={30}
              rowCount={this.state.regions.length}
              width={1000}
              ref={(grid) => this._grid = grid}
            />
            <button onClick={this.loadAnother}>change</button>
          </div>
        </div>
      </div>
    );
  }
})

export default App