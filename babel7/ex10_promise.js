function asyncFn(foo) {
    return new Promise((resolve, reject) => {
        if (foo === 7) {
            return resolve('lucky winner');
        }
 
        return reject('oh too bad!');
    });
}
 
asyncFn(7).then(
  success => console.log(success), 
  failure => console.log(failure)
);