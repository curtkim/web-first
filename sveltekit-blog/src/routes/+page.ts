import type {Post} from '$lib/types'

export async function load({fetch}){
  const response =await fetch('api/posts')
  const posts: Post[] = await response.json()
  return {posts}
}

/*
import type { PageLoad } from './$types';
export const load: PageLoad =({fetch}) => {
  const response= await fetch('api/posts')
  const posts: Post[] = await response.json()
  return {posts}
}
*/