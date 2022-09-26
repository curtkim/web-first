<script setup>

</script>

<template>
  <p>
    <router-link to="/">Go to Home</router-link>
    <router-link to="/detail/1">Go to 1</router-link>
    <router-link to="/about">Go to About</router-link>
  </p>
  <router-view></router-view>
</template>

<style scoped>
	p a {
		margin: 10px;
	}
</style>

