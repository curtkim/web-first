<template>
    <div class="box-wrapper">
        <div class="box" :style="{top: bTop + 'px', left: bLeft + 'px', width: bWidth + 'px', height: bHeight + 'px'}">
        </div>
    </div>
</template>

<script>
    export default {
        name: "Box",
        props: [
            'b-top', 'b-left', 'b-width', 'b-height'
        ]
    }
</script>

<style scoped>
    .box {
        position: absolute;
        border: 2px #90ee90 solid;
        z-index: 3;
    }
    .box:hover, .box.active {
        background-color: rgba(144, 238, 144, .2);
    }
</style>