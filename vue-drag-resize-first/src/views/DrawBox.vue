<template>
  <div class="draw_box">
    <div id="image-wrapper" :style="{backgroundImage: `url(/dataset-car.jpg)`, 'background-repeat': 'no-repeat'}" @mousedown="startDrawingBox" @mousemove="changeBox" @mouseup="stopDrawingBox">
        <Box v-if="drawingBox.active" 
            :b-width="drawingBox.width" 
            :b-height="drawingBox.height" 
            :b-top="drawingBox.top" 
            :b-left="drawingBox.left"/>
        <Box v-for="(box, i) in boxes" :key="i" 
            :b-top="box.top" 
            :b-left="box.left" 
            :b-width="box.width" 
            :b-height="box.height"/>
    </div>
  </div>
</template>

<script>
    import Box from "../components/Box";
    import {pick} from 'lodash';

    const getCoursorLeft = (e) => {
        return e.pageX - 10;
    };

    const getCoursorTop = (e) => {
        return e.pageY - 10;
    };

    export default {
        name: 'DrawBox',
        components: {Box},
        data: function () {
            return {
                drawingBox: {
                    active: false,
                    top: 0,
                    left: 0,
                    height: 0,
                    width: 0
                },
                boxes: []
            }
        },
        methods: {
            startDrawingBox(e) {
                this.drawingBox = {
                    width: 0,
                    height: 0,
                    top: getCoursorTop(e),
                    left: getCoursorLeft(e),
                    active: true,
                };
                console.log(this.drawingBox)
            },
            changeBox(e) {
                if (this.drawingBox.active) {
                    this.drawingBox = {
                        ...this.drawingBox,
                        width: getCoursorLeft(e) - this.drawingBox.left,
                        height: getCoursorTop(e) - this.drawingBox.top,
                    };
                }
            },
            stopDrawingBox() {
                if (this.drawingBox.active) {
                    if (this.drawingBox.width > 5) {
                        this.boxes.push({...pick(this.drawingBox, ['width', 'height', 'top', 'left'])});
                    }
                    this.drawingBox = {
                        active: false,
                        top: 0,
                        left: 0,
                        height: 0,
                        width: 0
                    }
                }
            },
        }
    }
</script>

<style scoped>
#image-wrapper {
    height: 1000px;
}

</style>