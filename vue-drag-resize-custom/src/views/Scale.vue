<template>
  <div class="home">
    <div :style="imageContainerStyle" ref="imageContainer">
        <img src="dataset-car.jpg" draggable="false"/>
    </div>

    <VueDragResize :isActive="true" 
            :w="width*zoom" :h="height*zoom" 
            :x="left*zoom" :y="top*zoom"
            :parentLimitation="true"
            :parentW="image_width*zoom" :parentH="image_height*zoom"
            v-on:resizing="resize" v-on:dragging="resize">
        <p>{{ top }} x {{ left }} </p>
        <p>{{ width }} x {{ height }}</p>
    </VueDragResize>
  </div>
</template>

<script>
// @ is an alias to /src
import VueDragResize from '@/components/vue-drag-resize.vue';

export default {
    name: 'app',

    components: {
        VueDragResize
    },

    mounted() {
        console.log('mounted')
        this.$refs.imageContainer.addEventListener("wheel", this.myWheel);
    },
    beforeDestroy(){
        this.$refs.imageContainer.removeEventListener("wheel", this.myWheel);
    },

    data() {
        return {
            image_width: 1200,
            image_height: 1200,
            zoom: 0.5,

            width: 200,
            height: 200,
            top: 0,
            left: 0
        }
    },

    computed: {
        imageContainerStyle() {
            return {
                'transform-origin': 'top left',
                transform: `matrix(${this.zoom}, 0, 0, ${this.zoom}, 0, 0)`
            }
        },
    },

    methods: {
        resize(newRect) {
            console.log('resize', newRect)
            this.width = newRect.width/this.zoom;
            this.height = newRect.height/this.zoom;
            this.top = newRect.top/this.zoom;
            this.left = newRect.left/this.zoom;
        },
        myWheel(e){
            e.preventDefault();            
            e.stopPropagation();

            if( e.deltaY > 0)
                this.zoom += 0.1
            else if( e.deltaY < 0)
                this.zoom -= 0.1
            //console.log(this.zoom)
            return false
        }
    }
}
</script>

<style scoped>
.home {
    border: 1px solid silver;
    position: relative;
}

.vdr {
    background-color: rgba(255, 0, 0, 0.6);
}
</style>