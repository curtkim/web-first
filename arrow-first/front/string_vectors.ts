import { makeVector, vectorFromArray, Dictionary, Uint8, Utf8 } from "apache-arrow";

const uft8Vector = vectorFromArray(['foo', 'bar', 'baz'], new Utf8);

//const dictionaryVector1 = vectorFromArray(['foo', 'bar', 'baz', 'foo', 'bar']);

const dictionaryVector2 = makeVector({
    data: [0, 1, 2, 0, 1],  // indexes into the dictionary
    dictionary: uft8Vector,
    type: new Dictionary(new Utf8, new Uint8)
});

//console.log(dictionaryVector1)
console.table(dictionaryVector2)

// console.log(dictionaryVector2.length)
// console.log(dictionaryVector2.get(0))
// console.log(dictionaryVector2.byteLength)
// console.log(dictionaryVector2.indexOf("foo"))
// for (let i in dictionaryVector2) {
//   console.log(i); // "0", "1", "2",
// }