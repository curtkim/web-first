import {redirect} from "@sveltejs/kit";

export const load = async (event) => {
  const session = await event.locals.getSession();

  if( !session){
    throw redirect(307, 'auth/signin')
  }
  console.log(session);
  /*
  {
    user: {
      name:'',
      email: '',
      image: ''
    },
    expires: ''
  }
  */
  return {
    session
  };
}