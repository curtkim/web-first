// from https://proinlab.com/archives/2138
'use strict';

let asyncFunction1 = (message)=> new Promise((resolve)=> {
    setTimeout(()=> {
        console.log('fn-1', message);
        resolve('fn-1');
    }, 1000);
});

let asyncFunction2 = (message)=> new Promise((resolve)=> {
    setTimeout(()=> {
        console.log('fn-2', message);
        resolve('fn-2');
    }, 500);
});

async function main(){
  let data = await asyncFunction1('hello')
  console.log(data)
  data = await asyncFunction2('world')
  console.log(data)
}

main()