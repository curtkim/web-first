function foo(options) {
  let {count, msg} = options;
  
  for(let i=0;i<count;i++) {
    console.log(`${i + 1}. ${msg}`);
  }
}
 
foo({count: 3, msg: 'something', junk: 88});