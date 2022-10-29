import {hello} from './hello'
import {promises as fs} from 'fs';
import fetch from 'cross-fetch'

async function writeTemp() {
  await fs.writeFile("tmp.txt", "123")
}

async function fetchGoogle() : Promise<string> {
  const response = await fetch('https://github.com/');
  const body = await response.text();
  return body;
}

console.log(hello())
writeTemp()
fetchGoogle().then(body =>{
  console.log(body)
})
