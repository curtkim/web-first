import {reactive, shallowReactive, ref, watch, computed} from 'vue'
import _ from 'lodash'
const setTimeout2 = (t, f)=>setTimeout(f, t)

const data = reactive([
  {id: 1, name:'test'},
  {id: 2, name:'test2'},
])

watch(
  ()=> _.cloneDeep(data), 
  (value, old)=>{
    console.log('old', old)
    console.log('new', value)
  }, 
  { deep: true }
)

data.push({id: 3, name:'test4'})
//data[0].name = 'test1'
//data.splice(1, 0, {id:3, name:'test3'})

setTimeout2(1, ()=> {
  console.log(data)
})