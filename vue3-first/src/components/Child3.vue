<script>
import { ref } from 'vue'

export default {
  name: 'Child3',
  setup(props, {emit}){
    const c = 1
    const d = ref(2)

    return {
      c, d
    }
  },
}
</script>

<template>
  <div>
    Child3<br/>
    {{ c }}
  </div>
</template>

<style scoped>
</style>
