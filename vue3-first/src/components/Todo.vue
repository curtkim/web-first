<script>

export default {
  props: {
		list: Array,
	},
  created() {
  }
}

</script>

<template>
	<ul>
		<li v-for="item in list">
			{{ item }}
		</li>
	</ul>
</template>

<style scoped>
</style>
