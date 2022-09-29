import {reactive} from 'vue'

const raw = {}
const proxy = reactive(raw)

console.log(proxy === raw) // false

console.log(reactive(raw) === proxy) // true

// calling reactive() on a proxy returns itself
console.log(reactive(proxy) === proxy) // true
