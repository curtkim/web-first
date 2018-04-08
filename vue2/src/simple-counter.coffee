###
async = require 'async'

async.eachSeries [1,2,3],
  (it, cb)->
    console.log it
    cb()
  (err)->
    console.log 'done'
###

module.exports =
  name: 'simple-counter'
  template: """
    <button v-on:click="counter += 1">{{ counter }}</button>
  """
  data: ()->
    {counter: 0}
