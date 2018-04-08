var Promise = require('promise');

var readFile = Promise.denodeify(require('fs').readFile);
// now `readFile` will return a promise rather than expecting a callback

/*
function readJSON(filename, callback){
  // If a callback is provided, call it with error as the first argument
  // and result as the second argument, then return `undefined`.
  // If no callback is provided, just return the promise.
  return readFile(filename, 'utf8').then(JSON.parse).nodeify(callback);
}
*/

readFile('package.json', 'utf8').then(JSON.parse).then(function(result){
  console.log(result)
})