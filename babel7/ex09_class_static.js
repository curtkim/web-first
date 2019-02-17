class Foo {
   constructor(a) {
      this.a = a * Foo.modifier;
   }
 
   static get modifier() { return 2; }
 
   static bar(instance) { if (instance instanceof Foo) { console.log('my baby!'); } }
}
 
let foo = new Foo(7);
 
console.log(foo.a); // 14
 
Foo.bar(foo); // my baby!