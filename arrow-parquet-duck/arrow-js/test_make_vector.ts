import { makeVector } from "apache-arrow";
import assert from 'node:assert/strict';


const LENGTH = 2000;

const rainAmounts = Float32Array.from(
    { length: LENGTH },
    () => Number((Math.random() * 20).toFixed(1)));

const vector = makeVector(rainAmounts);

const typed = vector.toArray()

assert(typed instanceof Float32Array);

for (let i = -1, n = vector.length; ++i < n;) {
    assert(vector.get(i) === typed[i]);
}

for(const item of vector)
    console.log(item)


