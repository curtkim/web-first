class Animal {
    constructor(name, voice) {
        this.name = name;
        this.voice = voice;
 
        this._eyes = 2;
    }
 
    get eyes() {
        return this._eyes;
    }
 
    speak() {
        console.log(`The ${this.name} says ${this.voice}.`);
    }
}
 
var foo = new Animal('dog', 'woof');
foo.speak(); // The dog says woof.
console.log(`${foo.name} has ${foo.eyes} eyes.`); // dog has 2 eyes