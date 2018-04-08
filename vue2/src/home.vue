<template>
  <div id="home">

    <el-tabs type="card" v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="User" name="first">User</el-tab-pane>
      <el-tab-pane label="Config" name="second">Config</el-tab-pane>
      <el-tab-pane label="Role" name="third">Role</el-tab-pane>
      <el-tab-pane label="Task" name="fourth">Task</el-tab-pane>
    </el-tabs>

    <div>
      <el-button>Default Button</el-button>
      <el-button type="primary">Primary Button</el-button>

      <el-button type="text" @click="dialogVisible = true">click to open the Dialog</el-button>

      <el-dialog
        title="Tips"
        :visible.sync="dialogVisible"
        size="tiny">
        <span>This is a message</span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="dialogVisible = false">Confirm</el-button>
        </span>
      </el-dialog>
    </div>

    <svg-circle :radius='radius'></svg-circle>
    <simple-counter></simple-counter>
    <el-slider v-model="level" :max="14"></el-slider>

    <d3-circle></d3-circle>

    <d3-graph></d3-graph>
    <mymap :level='level' :center='center'></mymap>
  </div>
</template>

<script lang="coffee">
module.exports = {
  name: 'home'
  data: ()->
    {
      msg: 'Welcome to Your Vue.js App',
      radius: 80,
      center: [195063,442898]
      level: 5
      dialogVisible: false
      activeName: 'first'
    }
  methods:
    handleClick : (tab, event)->
      console.log(tab, event)
}
</script>

<style>
#home {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
