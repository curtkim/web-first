import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(VueRouter)
Vue.use(Element, { size: 'mini' });
Vue.config.productionTip = false

import HelloWorld from './components/HelloWorld.vue'
import Map from './components/Map.vue'
import Form from './components/Form.vue'

const routes = [
  { path: '/foo', component: HelloWorld },
  { path: '/map', component: Map },
  { path: '/form', component: Form },
]

const router = new VueRouter({
  routes // short for `routes: routes`
})

new Vue({
  render: h => h(App),
  router
}).$mount('#app')
