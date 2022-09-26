import { createApp } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'

import './style.css'
import App from './App.vue'
import Home from './views/Home.vue'
import About from './views/About.vue'
import Detail from './views/Detail.vue'


const routes = [
	  { path: '/', component: Home },
		{ path: '/detail/:id', component: Detail, props:true},
	  { path: '/about', component: About },
]

const router = createRouter({
	   history: createWebHashHistory(),
	     routes, // short for `routes: routes`
	 })
	

const app = createApp(App)
app.use(router)
app.mount('#app')

