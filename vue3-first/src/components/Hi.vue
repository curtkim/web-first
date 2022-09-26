<script>

export default {
  props: ['msg'],
  created() {
    // props are exposed on `this`
    console.log(this.msg)
  }
}

</script>

<template>
  <h1>hi {{ msg }}</h1>
</template>

<style scoped>
</style>
