import React, { Component } from 'react';

import 'bootstrap/dist/css/bootstrap.css'

export default class App extends Component {
  render() {
    return (
      <div className='myheader'>
        <h1>Hello, world!!</h1>
        <button className="btn btn-info" type="button">
          <span className="glyphicon glyphicon-refresh">test</span>
        </button>
      </div>
    );
  }
}