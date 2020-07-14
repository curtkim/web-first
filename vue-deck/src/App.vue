<template>
  <div id="app">   
    <PointCloud2/>
    <!-- 
    <HelloWorld msg="Welcome to Your Vue.js App"/>
    -->
  </div>
</template>

<script>
import PointCloud2 from './components/PointCloud2.vue';
//import HelloWorld from './components/HelloWorld.vue';

export default {
  name: 'App',
  components: {
    PointCloud2
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
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin-top: 60px;
}

</style>
