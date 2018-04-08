<template>
  <div id="extent-height" v-bind:style="{height: height+'px'}">

  </div>
</template>

<script>

export default {
  props: {

  },
  mounted(){
    this.updateHeight()
    let _self = this
    this.handler = function(){
      _self.updateHeight()
    }
    window.addEventListener("resize", this.handler);
  },
  beforeDestroy(){
    window.removeEventListener("resize", this.handler);
  },
  data(){
    return {
      height: 0
    }
  },
  methods: {
    updateHeight(){
      let w = window
      let d = document
      let e = d.documentElement
      let g = d.getElementsByTagName('body')[0]
      let y = w.innerHeight|| e.clientHeight|| g.clientHeight
      this.height = y - this.$el.getBoundingClientRect().y - 10 // TODO
    }
  }
}
</script>

<style scoped>
</style>
