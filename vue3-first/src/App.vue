<script setup>

</script>

<template>
  <p>
    <router-link to="/">Home</router-link>
    <router-link to="/detail/1">router path variable</router-link>
    <router-link to="/asyncState">Async State</router-link>
    <router-link to="/about">Slot</router-link>
    <router-link to="/provide_inject">Provide Inject</router-link>
    <router-link to="/store">Store</router-link>
    <router-link to="/component_ref">ComponentRef</router-link>
    <router-link to="/props">Props</router-link>
  </p>
  <router-view></router-view>
</template>

<style scoped>
	p a {
		margin: 10px;
	}
</style>

