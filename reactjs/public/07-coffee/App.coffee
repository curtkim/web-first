React = require 'react'
dom = React.DOM
{div, span, form, input, button} = React.DOM


App = React.createClass
  render: ->
    div {className: 'commentList'},
      span null, "abc"
      div null, "123"
      form {className: 'commentForm', onSubmit: @handleSubmit},
        input(
          type: 'text'
          placeholder: 'Your name'
        ),
        input(
          type: 'text'
          placeholder: 'Say something fun !'
        ),
        input(
          type: 'submit'
          value: 'Post'
        )

Demo = React.createClass
  getInitialState: ()->
    { count: 0 }
  handleClick: ()->
    @setState {
      count: @state.count + 1
    }
  render: ()->
    button {onClick: @handleClick}, "Click me! Number of clicks #{@state.count}"


class ExampleComponent extends React.Component
  constructor: (props) ->
    super props
    @state =
      count: 0

  @defaultProps: ()->
    bar: 'baz'

  handleClick: ()->
    @setState {
      count: @state.count + 1
    }

  render: ->
    button {onClick: @handleClick.bind(@)}, "Click me! Number of clicks #{@state.count}"

module.exports = App
