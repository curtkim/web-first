var axios = require('axios')

async function main(){
  let res = await axios.get('http://www.daum.net')
  console.log(res.status)
  data = await axios.get('http://www.daum.net')
  console.log(res.status)
}

main()