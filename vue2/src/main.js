import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

// 1. Component
import 'element-ui/lib/theme-default/index.css'
import ElementUI from 'element-ui'
Vue.use(ElementUI)

import SvgCircle from './svg-circle.vue'
import SimpleCounter from './simple-counter'
import MyMap from './mymap'
import D3Graph from './d3-graph.vue'
import D3Circle from './d3-circle.vue'
import Chart3D from './chart-3d'

Vue.component(SvgCircle.name, SvgCircle)
Vue.component(SimpleCounter.name, SimpleCounter)
Vue.component('mymap', MyMap)
Vue.component(D3Graph.name, D3Graph)
Vue.component(Chart3D.name, Chart3D)



// 2. Route Component
import Home from './home.vue'
import Etc from './etc.vue'
const Foo = { template: '<div>foo</div>' }
const Bar = { template: '<div>bar</div>' }

const routes = [
  { path: '/foo', component: Foo },
  { path: '/bar', component: Bar },
  { path: '/home', component: Home },
  { path: '/etc', component: Etc }
]

// 3. Router & App
const router = new VueRouter({
  routes // short for `routes: routes`
})

const app = new Vue({
  router
}).$mount('#app')