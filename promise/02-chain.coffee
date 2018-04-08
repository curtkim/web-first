Promise = require 'promise'

setTimeout2 = (t, f)-> setTimeout f,t
readFile = Promise.denodeify(require('fs').readFile)

parseAsync = (result)->
  new Promise (resolve, reject)->
    setTimeout2 1000, ()->
      if Math.random() > 0.5
        resolve JSON.parse(result)
      else
        reject 'not lucky'

readFile('package.json', 'utf8')
  .then(parseAsync)
  .then (result)->
    console.log(result)
  .catch (err)->
    console.log("error : #{err}")