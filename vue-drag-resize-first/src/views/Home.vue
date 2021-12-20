<template>
  <div class="home">
    <img src="/dataset-car.jpg" draggable="false">

    <VueDragResize :isActive="true" 
        :x="left" :y="top" :w="width" :h="height" 
        v-on:resizing="resize" v-on:dragging="resize" 
        :parentLimitation="false">
      <p>{{ top }} х {{ left }} </p>
      <p>{{ width }} х {{ height }}</p>      
    </VueDragResize>
  </div>
</template>

<script>
// @ is an alias to /src
//import HelloWorld from '@/components/HelloWorld.vue'

import VueDragResize from 'vue-drag-resize';


export default {
  name: 'Home',
  components: {
    VueDragResize
  },
  data() {
      return {
          width: 200,
          height: 200,
          top: 100,
          left: 100
      }
  },

  methods: {
      resize(newRect) {
          this.width = newRect.width;
          this.height = newRect.height;
          this.top = newRect.top;
          this.left = newRect.left;
      }
  }  
}
</script>

<style>
.home {
  border: 1px solid black;
}
.vdr {
  color: white;
  background-color: rgba(0,0,255,0.6);
}
</style>
