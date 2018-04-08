var request = require('request-promise')

/*
request('http://www.google.com')
  .then((data)=> console.log(data))
  .catch((err)=> console.err(err))
*/

async function main(){
  let data = await request('http://www.daum.net')
  console.log(typeof(data))
  data = await request('http://www.daum.net')
  console.log(typeof(data))
}

main()