const fetch = require('node-fetch');

async function main()
{
  const res = await fetch('https://api.github.com/users/KrunalLathiya')
  const data = await res.json();
  console.log(data);
}

main();