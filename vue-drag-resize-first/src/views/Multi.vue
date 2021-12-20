<template>
  <div class="multi">
    <div class="workspace">
        <VueDragResize v-for="(rect, index) in rects"
                        :key="index"
                        :w="rect.width"
                        :h="rect.height"
                        :x="rect.left"
                        :y="rect.top"
                        :parentW="listWidth"
                        :parentH="listHeight"
                        :axis="rect.axis"
                        :isActive="rect.active"
                        :minw="rect.minw"
                        :minh="rect.minh"
                        :isDraggable="rect.draggable"
                        :isResizable="rect.resizable"
                        :parentLimitation="rect.parentLim"
                        :snapToGrid="rect.snapToGrid"
                        :aspectRatio="rect.aspectRatio"
                        :z="rect.zIndex"
                        :parentScaleX="1"
                        :parentScaleY="1"
                        :contentClass="rect.class"
                        v-on:activated="activateEv(index)"
                        v-on:deactivated="deactivateEv(index)"
                        v-on:dragging="changePosition($event, index)"
                        v-on:resizing="changeSize($event, index)"
        >
            <div class="filler" :style="{backgroundColor:rect.color}"></div>
        </VueDragResize>
    </div>
    <div class="list">
        <div v-for="(rect, index) in rects" :key="index">
            {{index}} {{rect.left}} {{rect.top}} {{rect.width}} {{rect.height}} {{rect.active}}
        </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
//import HelloWorld from '@/components/HelloWorld.vue'

import VueDragResize from 'vue-drag-resize';


export default {
  name: 'Multi',
  components: {
    VueDragResize
  },
  data() {
      return {
        listWidth: 0,
        listHeight: 0,
        rects: [
            {
                'width': 200,
                'height': 150,
                'top': 10,
                'left': 10,
                'draggable': true,
                'resizable': true,
                'minw': 10,
                'minh': 10,
                'axis': 'both',
                'parentLim': true,
                'snapToGrid': false,
                'aspectRatio': false,
                'zIndex': 1,
                'color': '#EF9A9A',
                'active': false
            },
            {
                'width': 200,
                'height': 150,
                'top': 170,
                'left': 220,
                'draggable': true,
                'resizable': true,
                'minw': 10,
                'minh': 10,
                'axis': 'both',
                'parentLim': true,
                'snapToGrid': false,
                'aspectRatio': false,
                'zIndex': 1,
                'color': '#E6C27A',
                'active': false,
                'class': 'box-shaddow'
            },
            {
                'width': 200,
                'height': 150,
                'top': 10,
                'left': 220,
                'draggable': true,
                'resizable': true,
                'minw': 10,
                'minh': 10,
                'axis': 'both',
                'parentLim': true,
                'snapToGrid': false,
                'aspectRatio': false,
                'zIndex': 2,
                'color': '#AED581',
                'active': false
            },
            {
                'width': 200,
                'height': 150,
                'top': 170,
                'left': 10,
                'draggable': true,
                'resizable': true,
                'minw': 10,
                'minh': 10,
                'axis': 'both',
                'parentLim': true,
                'snapToGrid': false,
                'aspectRatio': false,
                'zIndex': 3,
                'color': '#81D4FA',
                'active': false
            }
        ]
    }
  },

  methods: {
        activateEv(index) {
            for (var idx in this.rects) {
                this.rects[idx].active = false
            }
            this.rects[index].active = true
            //this.$store.dispatch('rect/setActive', {id: index});
        },
        deactivateEv(index) {
            this.rects[index].active = false
            //this.$store.dispatch('rect/unsetActive', {id: index});
        },
        changePosition(newRect, index) {
            this.rects[index].top = newRect.top
            this.rects[index].left = newRect.left
            this.rects[index].width = newRect.width
            this.rects[index].height = newRect.height

            //console.log(newRect, index)
            // this.$store.dispatch('rect/setTop', {id: index, top: newRect.top});
            // this.$store.dispatch('rect/setLeft', {id: index, left: newRect.left});
            // this.$store.dispatch('rect/setWidth', {id: index, width: newRect.width});
            // this.$store.dispatch('rect/setHeight', {id: index, height: newRect.height});
        },
        changeSize(newRect, index) {
            this.rects[index].top = newRect.top
            this.rects[index].left = newRect.left
            this.rects[index].width = newRect.width
            this.rects[index].height = newRect.height

            //console.log(newRect, index)
            // this.$store.dispatch('rect/setTop', {id: index, top: newRect.top});
            // this.$store.dispatch('rect/setLeft', {id: index, left: newRect.left});
            // this.$store.dispatch('rect/setWidth', {id: index, width: newRect.width});
            // this.$store.dispatch('rect/setHeight', {id: index, height: newRect.height});
        }

    }  
}
</script>

<style>
    .filler {
        width: 100%;
        height: 100%;
        display: inline-block;
        position: absolute;
    }

    .workspace {
        position: absolute;
        top: 100px;
        bottom: 30px;
        left: 30px;
        right: 500px;
        box-shadow: 0 0 2px #AAA;
        background-color: white;
    }
    .list {
        position: absolute;        
        right: 10px;
        width: 400px;
    }

    .box-shaddow {
        box-shadow:  10px 10px 15px 0px rgba(125,125,125,1);
    }
</style>
