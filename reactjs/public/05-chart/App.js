import React, { Component } from 'react'
import Chart from './chart'
//import C3Chart from './c3chart'

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

  _getData: function(){
    var cube = this.state.cube

    let REGIONS = ['1101053', '1101054', '1101055', '1101056'];
    var i, len, plane, r, results, row;
    results = [];
    for (i = 0, len = REGIONS.length; i < len; i++) {
      r = REGIONS[i];
      row = [r].concat((function() {
        var j, len1, results1;
        results1 = [];
        for (j = 0, len1 = cube.length; j < len1; j++) {
          plane = cube[j];
          results1.push(plane[r][0]);
        }
        return results1;
      })());
      results.push(row);
    }
    return results;
  },

  loadAnother: function(){
    var _this = this
    $.getJSON('/public/data2.json', function(data){
      _this.setState({cube:data, regions:_this.state.regions})
    })
  },

  render: function() {
    let data = {
      columns: this._getData()
    }

    /*
    let options = {
      padding: {
        top: 20,
        bottom: 20,
        left: 40,
        right: 10
      },
      size: {
        width: 800,
        height: 600
      },
      labels: true,
      onClick: function(d) {
        let categories = this.categories(); //c3 function, get categorical labels
        console.log(d);
        console.log("you clicked {" + d.name + ": " + categories[d.x] + ": " + d.value + "}");
      }
    };
    */
    // <C3Chart data={data} type={'line'} options={options}/>

    return (
      <div className="container-fluid">
        <div className="row">
          <div className="col-sm-12 content">
            <div className="page-header">
              <h3>시간대별 배차 현황</h3>
            </div>

            <Chart data={data}/>

            <button onClick={this.loadAnother}>change</button>
          </div>
        </div>
      </div>
    );
  }
})

export default App