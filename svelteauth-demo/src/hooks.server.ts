import {SvelteKitAuth} from "@auth/sveltekit"; [SvelteKitAuth]
import GitHub from "@auth/core/providers/github";
import {GITHUB_ID, GITHUB_SECRET} from '$env/static/private'

////console.log(GITHUB_ID, GITHUB_SECRET);

export const handle = SvelteKitAuth({
  providers: [
    GitHub({
      clientId: GITHUB_ID,
      clientSecret: GITHUB_SECRET
    })
  ]
});