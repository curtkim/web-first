<script setup>
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import HelloWorld from '../components/HelloWorld.vue'
import Hi from '../components/Hi.vue'
import Todo from '../components/Todo.vue'
import FancyButton from '../components/FancyButton.vue'

import { reactive } from 'vue'
import {useMouse} from '../composable/mouse.js'

const {x, y} = useMouse()
const todoList = reactive([ "a111", "b222" ])

</script>

<template>
  <HelloWorld msg="Vite + Vue" /><br/>
	<Hi msg="curt"/>
	<Todo :list="todoList"/>

  <FancyButton>
    <span style="color:red">Click me!</span>
  </FancyButton>
  <div>
    Mouse position : {{ x }} {{ y }}
  </div>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
