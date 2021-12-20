import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Multi from '../views/Multi.vue'
import Scale from '../views/Scale.vue'
import DrawBox from '../views/DrawBox.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/multi',
    name: 'Multi',
    component: Multi
  },
  {
    path: '/scale',
    name: 'Scale',
    component: Scale
  },
  {
    path: '/draw_box',
    name: 'DrawBox',
    component: DrawBox
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
