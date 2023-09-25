<script>
import {signIn, signOut} from '@auth/sveltekit/client';
import {page} from '$app/stores';
</script>

<p>
  {#if Object.keys($page.data.session || {}).length}

    {#if $page.data.session?.user?.image}
      <img
          src={$page.data.session?.user?.image}
          alt={$page.data.session?.user?.name}
          style="width: 64px; height: 64px;"/>
      <br/>
    {/if}
    <span>
      <strong>{$page.data.session?.user?.email || $page.data.session?.user?.name}</strong>
    </span>
    <br/>
    <span>Session expires: {new Date($page.data.session?.expires ?? 0).toUTCString()}</span>
    <br/>
    <button on:click={() => signOut()} class="button">Sign out</button>
  {:else}
    <span>You are not signed in</span><br/>
    <button on:click={() => signIn('github')}>Sign In with GitHub</button>
  {/if}
</p>