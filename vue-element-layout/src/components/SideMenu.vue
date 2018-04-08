<template>
  <div>
    <el-menu :default-openeds="['1', '3']">
      <el-submenu index="1">
        <template slot="title">
          KPI
        </template>
        <el-menu-item index="1-1">Option1</el-menu-item>
        <el-menu-item index="1-2">Option2</el-menu-item>
        <el-menu-item index="1-3">Option3</el-menu-item>
        <el-menu-item index="1-4">Option4</el-menu-item>
      </el-submenu>
      <el-submenu index="2">
        <template slot="title">
          Navigator Two
        </template>
        <el-menu-item index="2-1">Option 1</el-menu-item>
        <el-menu-item index="2-2">Option 2</el-menu-item>
        <el-menu-item index="2-3">Option 3</el-menu-item>
      </el-submenu>
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-setting"></i>Navigator Three
        </template>
        <el-menu-item index="3-1">Option 1</el-menu-item>
        <el-menu-item index="3-2">Option 2</el-menu-item>
        <el-menu-item index="3-3">Option 3</el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  props: {
    msg: String
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
