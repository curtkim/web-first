<script>
export default {
  props: ['id'],
  setup(props) {
    // setup() receives props as the first argument.
    console.log(props.id)
  }
}

</script>

<template>
	<div>Detail</div>
	<div>{{ id }} </div>
</template>

<style scoped>
</style>
