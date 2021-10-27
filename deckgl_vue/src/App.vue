<template>
  <div id="app">   
    <KittiWorld/>
    <!-- 
    <PointCloud3/>    
    <MeshTest/>
    <PointCloud2/>
    <HelloWorld msg="Welcome to Your Vue.js App"/>
    -->
  </div>
</template>

<script>
import PointCloud from './components/PointCloud.vue';
import PointCloud2 from './components/PointCloud2.vue';
import PointCloud3 from './components/PointCloud3.vue';
import MeshTest from './components/MeshTest.vue';
import HelloWorld from './components/HelloWorld.vue';
import KittiWorld from './components/KittiWorld.vue';

export default {
  name: 'App',
  components: {
    //PointCloud3, 
    //PointCloud2, 
    //MeshTest,
    KittiWorld,
  },

  data() {
    return {      
    }
  },
}
</script>

<style>
body {
  margin : 0;
  padding : 0;
  overflow: hidden;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
