<script lang="ts">
  import {formatDate} from '$lib/utils'
  import * as config from '$lib/config'

  export let data

</script>
<svelte:head>
  <title>{config.title}</title>
</svelte:head>

<section>
  <ul class="posts">
    {#each data.posts as post}
      <li class="post">
        <a href="{post.slug}" class="title">{post.title}</a>
        <a class="date">{formatDate(post.date)}</a>
        <a class="description">{post.description}</a>
      </li>
    {/each}
  </ul>
</section>

<style>
  .posts {
    display: grid;
    gap: 2em;
  }
  .post{
    max-inline-size: var(--size-content-3);
  }
  .post:not(:last-child){
    border-bottom: 1px solid var(--border);
    padding-bottom: var(--size-7);
  }
  .title{
    font-size: var(--font-size-fluid-3);
    text-transform: capitalize;
  }
  .date {
    color: var(--text-2)
  }
  .description{
    margin-top: var(--size-3)
  }
</style>