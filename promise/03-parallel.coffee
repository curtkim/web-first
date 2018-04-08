Promise = require 'promise'

setTimeout2 = (t, f)-> setTimeout f,t

doit = (name)->
  new Promise (resolve, reject)->
    setTimeout2 Math.random()*1000, ()->
      resolve "#{name} done"

Promise.all([
  doit('1')
  doit('2')
  doit('3')
  doit('4')
])
.then (arr) ->
  console.log arr