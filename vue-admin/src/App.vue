<template>
  <div id="app">
    <div id="left">
      <ul>
        <li>
          <i class="el-icon-edit"></i>
          <router-link to="/foo">Go to Foo</router-link>
        </li>
        <li>
          <i class="el-icon-share"></i>
          <router-link to="/map">Go to Map</router-link>
        </li>
        <li>
          <i class="el-icon-share"></i>
          <router-link to="/form">Go to Form</router-link>
        </li>
      </ul>
    </div>
    <div id="container">
      <div id="title">타이틀</div>
      <div id="container-inner">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'app',
}
</script>

<style>
body {
  margin:0;
  padding:0;
}
#app {
  position: relative;
  height: 100%;
  width: 100%;
}
#left {
  background-color: #304156;
  height: 100%;
  width: 180px!important;
  position: fixed;
  z-index: 1001;
}
#left li, #left li a {
  color: #fff;
}
#container {
  margin-left: 180px;
  min-height: 100%;
}
#container-inner {
  padding: 10px;
}
.el-submenu .el-menu-item {
  min-width: 180px!important;
}
.el-menu--collapse .el-menu .el-submenu{
  min-width: 180px!important;
}

#title {
  padding: 10px;
  font-size: 22px;
  border-bottom: 1px solid #ddd;
}
</style>
