import {reactive, computed} from 'vue'
const setTimeout2 = (t, f)=>setTimeout(f, t)

const state = reactive({count : 0})

const A3 = computed(()=> state.count * 2) 

function increment(){
  state.count++
}

console.log(state.count)
console.log(A3.value)

increment()
console.log(state.count)
console.log(A3.value)

setTimeout2(1, ()=> {
  console.log(state.count)
  console.log(A3.value)
})