import { createApp } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'

import './style.css'
import App from './App.vue'
import Home from './views/Home.vue'
import About from './views/About.vue'
import Detail from './views/Detail.vue'
import AsyncState from './views/AsyncState.vue'
import ProvideInject from './views/ProvideInject.vue'
import Store from './views/Store.vue'
import ComponentRef from './views/ComponentRef.vue'
import Props from './views/Props.vue'


const routes = [
	  { path: '/', component: Home },
		{ path: '/detail/:id', component: Detail, props:true},
	  { path: '/asyncState', component: AsyncState },
	  { path: '/about', component: About },
	  { path: '/provide_inject', component: ProvideInject },
	  { path: '/store', component: Store},
	  { path: '/component_ref', component: ComponentRef},
	  { path: '/props', component: Props},
]

const router = createRouter({
	   history: createWebHashHistory(),
	     routes, // short for `routes: routes`
	 })
	

const app = createApp(App)
app.use(router)
app.mount('#app')

