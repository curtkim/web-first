<template>
  <div id="app">
    <el-container>
      <el-header>
        <h1>택시통계</h1>
      </el-header>
      <el-container>
        <el-aside width="240px">
          <side-menu></side-menu>
        </el-aside>
        <el-container>
          <el-main>

            <h3>데이터</h3>
            <table class="table">
              <thead>
                <tr>
                  <th style='width:140px'>날짜</th>
                  <th style='width:120px'>이름</th>
                  <th>주소</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in tableData">
                  <td>{{ row.date }}</td>
                  <td>{{ row.name }}</td>
                  <td>{{ row.address }}</td>
                </tr>
              </tbody>
            </table>

            <!--
            <el-table :data="tableData">
              <el-table-column prop="date" label="Date" width="140"></el-table-column>
              <el-table-column prop="name" label="Name" width="120"></el-table-column>
              <el-table-column prop="address" label="Address"></el-table-column>
            </el-table>
            -->
          </el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import SideMenu from './components/SideMenu.vue'

export default {
  name: 'app',
  components: {
    HelloWorld, SideMenu
  },
  data() {
    const item = {
      date: '2016-05-02',
      name: 'Tom',
      address: 'No. 189, Grove St, Los Angeles'
    };
    return {
      tableData: Array(10).fill(item)
    }
  }
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
}

#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.el-header {
  background-color: #563d7c;
  color: #fff;
  text-align: left;
}

.el-aside {
  background-color: #fff;
  color: #333;
  text-align: left;
}

.el-main {
  background-color: #fff;
  color: #333;
  border-left : solid 1px #e6e6e6;
}

#app .el-menu {
  border-right : none;
}

table.table {
  width: 100%;
}
table.table td, table.table th {
  text-align: left;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
  font-size: 14px;
}

</style>

