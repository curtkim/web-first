<template>
  <div class="about">
    <div class="image_wrapper" ref="wrapper">
      <img src="dataset-car.jpg" draggable="false"/>

      <VueDragResize :isActive="true" 
              :w="width" :h="height" 
              :x="left" :y="top"
              :parentLimitation="true"
              :parentW="1200" :parentH="1200"
              v-on:resizing="resize" v-on:dragging="resize">

          <p>{{ top }} x {{ left }} </p>
          <p>{{ width }} x {{ height }}</p>
      </VueDragResize>

    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import VueDragResize from '@/components/vue-drag-resize.vue';

export default {
  name: 'Overflow',

  components: {
      VueDragResize
  },

  data() {
    return {
      width: 200,
      height: 200,
      top: 0,
      left: 0
    }
  },

  mounted() {
    var el = this.$refs.wrapper
    console.log(el.clientWidth, el.offsetWidth, el.scrollWidth)
    console.log(el.clientHeight, el.offsetHeight, el.scrollHeight)
  },
  
  methods: {
    resize(newRect) {
        //console.log('resize', newRect)
        this.width = newRect.width;
        this.height = newRect.height;
        this.top = newRect.top;
        this.left = newRect.left;
    }
  }
}

</script>

<style scoped>
.image_wrapper {
  position: relative;
  width: 600px;
  height: 600px;
  overflow: auto;
}

.vdr {
    background-color: rgba(255, 0, 0, 0.4);
}

</style>