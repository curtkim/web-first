import {reactive, shallowReactive, ref, watch, computed} from 'vue'
import _ from 'lodash'
const setTimeout2 = (t, f)=>setTimeout(f, t)

const data = reactive({
  id: 1, 
  name:'test',
  age: 20,
  hobby: ['vue', 'math'],
})

watch(
  ()=> _.cloneDeep(data), 
  (value, old)=>{
    console.log('old', old)
    console.log('new', value)
  }, 
  { deep: true }
)

data.age++
data.hobby.push('music')
// 2번 수정하지만 watch callback은 한번 호출된다.

setTimeout2(1, ()=> {
  console.log(data)
})
