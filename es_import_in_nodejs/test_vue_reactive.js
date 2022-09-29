import {reactive, computed} from 'vue'
const setTimeout2 = (t, f)=>setTimeout(f, t)

const state = reactive({count : 0})

const A3 = computed(()=> state.count * 2)  // computed는 Ref를 반환하는 것 같다.

function increment(){
  state.count++
}

console.log(state.count)
console.log(A3.value)

increment()                 // increment는 state.count, A3.value에 바로 반영되었다.
console.log(state.count)
console.log(A3.value)

setTimeout2(1, ()=> {
  console.log(state.count)
  console.log(A3.value)
})