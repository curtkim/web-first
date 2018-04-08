import React, { Component } from 'react'
const c3 = require("c3")

var Chart = React.createClass({

  drawGraph: function() {
    let chart = c3.generate({
      bindto: '#chartContainer',
      data: this.props.data
    })
  },

  componentDidMount: function() {
    this.drawGraph();
  },

  componentDidUpdate: function () {
    this.drawGraph();
  },

  render: function() {
    return (
      <div>
        <div id="chartContainer"></div>
      </div>
    );
  }

})

export default Chart