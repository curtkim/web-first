import fetch from 'cross-fetch';

async function test() {
  const response = await fetch('https://github.com/');
  const body = await response.text();
  console.log(body);
}

test()