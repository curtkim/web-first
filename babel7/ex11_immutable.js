const person = {
  name: 'John',
  age: 28
}

const newPerson = {
  ...person,
  age: 30
}

const newPerson2 = Object.assign({}, person, {
  age: 32
})

console.log(newPerson === person) // false
console.log(newPerson) // { name: 'John', age: 30 }
console.log(newPerson2)