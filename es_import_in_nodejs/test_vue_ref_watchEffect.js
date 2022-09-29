import {ref, watchEffect, computed} from 'vue'

const setTimeout2 = (t, f)=>setTimeout(f, t)

const A0 = ref(0)
const A1 = ref(1)
const A2 = ref()
watchEffect(()=> {
  A2.value = A0.value + A1.value
})

const A3 = computed(()=> A0.value + A1.value) 


console.log(A2.value)
console.log(A3.value)

A0.value = 2

console.log(A2.value)     // 1
console.log(A3.value)     // 3

setTimeout2(1, ()=> {
  console.log(A2.value)   // 3
  console.log(A3.value)   // 3
})
