<script setup>
import { reactive } from 'vue'
import BaseLayout from '../components/BaseLayout.vue';

</script>

<template>
	<BaseLayout>
		<template #header>
			<h1>here might be a page header</h1>
		</template>
		<template #default>
			<p> body</p>
		</template>
		<template #footer>
			<p> footer : contact info</p>
		</template>
	</BaseLayout>
</template>

<style scoped>
</style>
