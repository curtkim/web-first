import Vue from 'vue'
import VueRouter from 'vue-router'
import Scale from '../views/Scale.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Scale',
    component: Scale
  },
  {
    path: '/overflow',
    name: 'Overflow',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "overflow" */ '../views/Overflow.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
