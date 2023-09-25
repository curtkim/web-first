<script>
  import { page } from '$app/stores';
  import {signIn, signOut} from "@auth/sveltekit/client"

  let email = '';

  const handleEmailSignIn = () => {
    signIn('email', {email, callbackUrl: '/protected'})
  }

  const handleGoogleSignIn = () => {
    signIn('google', {callbackUrl: '/protected' })
  }

  const handleSignOut = () => {
    signOut();
  }
</script>

<div class="container">

  {#if !$page.data.session}

    <button on:click={handleGoogleSignIn}>
      Continue with Google
    </button>

  {/if}

  {#if $page.data.session}

    <div>
      <button on:click={handleSignOut}>Sign out</button>
    </div>

  {/if}
</div>
