var vals = [1, 2];
 
function add(a, b) {
    return a + b;
}
 
console.log(add(...vals)); // 3

var others = [3, ...vals, 4]; // [3, 1, 2, 4]
console.log(others)
