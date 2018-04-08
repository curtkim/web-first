Vue = require('vue').default
VueRouter = require('vue-router').default
Vue.use(VueRouter)

# 1. Component
require 'element-ui/lib/theme-default/index.css'
ElementUI = require 'element-ui'
Vue.use(ElementUI)

SvgCircle = require './svg-circle.vue'
SimpleCounter = require './simple-counter'
MyMap = require('./mymap').default

Vue.component(SvgCircle.name, SvgCircle)
Vue.component(SimpleCounter.name, SimpleCounter)
Vue.component('mymap', MyMap)


# 2. Route Component
Home = require './home.vue'
Foo = { template: '<div>foo</div>' }
Bar = { template: '<div>bar</div>' }

routes = [
  { path: '/foo', component: Foo }
  { path: '/bar', component: Bar }
  { path: '/home', component: Home }
]

# 3. Router & App
router = new VueRouter({routes})

app = new Vue({
  router
}).$mount('#app')