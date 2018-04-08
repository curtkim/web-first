var p1 = Promise.resolve(3);
var p2 = 1337;
var p3 = new Promise(function(resolve, reject) {
  console.log('p3')
  setTimeout(resolve, 100, "foo");
});

var p4 = new Promise(function(resolve, reject) {
  console.log('p4')
  setTimeout(resolve, 10, "foo");
});

Promise.all([p1, p2, p3, p4]).then(function(values) {
  console.log(values); // [3, 1337, "foo"]
});