<script lang="ts">
  import Simple from "./lib/Simple.svelte";
  import WithTableUI from "./lib/WithTableUI.svelte";

  let isSimple = false;
</script>

<label>
  <input
      type="radio"
      name="isSimple"
      value={true}
      bind:group={isSimple}
  /> Simple UI

  <input
      type="radio"
      name="isSimple"
      value={false}
      bind:group={isSimple}
  /> Svelte-Table U
</label>
<hr/>

{#if isSimple}
  <Simple/>
{:else}
  <WithTableUI/>
{/if}

<style>

</style>