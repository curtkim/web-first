import usePropsAsObjectProperties from "./usePropsAsObjectProperties.js";

import {reactive, computed} from 'vue'

const props = reactive({msg:"test", count:0})
const {
  properties
} = usePropsAsObjectProperties(props)


function increment(){
  props.count++
}
function increment2(){
  properties.count++
}
const A3 = computed(()=> props.count * 2) 
const A4 = computed(()=> properties.count * 3) 

console.log(props)
console.log(properties)

increment()
increment2()
console.log(A3.value)
console.log(A4.value)