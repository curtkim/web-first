import {ref, watchEffect, computed} from 'vue'

const setTimeout2 = (t, f)=>setTimeout(f, t)

const A0 = ref(0)
const A1 = ref(1)
const A2 = ref()
const A3 = computed(()=> A0.value + A1.value) 


watchEffect(()=> {
  A2.value = A0.value + A1.value
})

A0.value = 2

setTimeout2(1, ()=> {
  console.log(A2.value)
  console.log(A3.value)
})
