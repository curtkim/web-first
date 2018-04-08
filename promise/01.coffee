Promise = require 'promise'

readFile = Promise.denodeify(require('fs').readFile)

readFile('package.json', 'utf8')
  .then(JSON.parse)
  .then (result)->
    console.log(result)
  .catch (err)->
    console.log("err #{err}")