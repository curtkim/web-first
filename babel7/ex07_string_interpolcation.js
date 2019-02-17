let nouns = ['ape', 'sandwich', 'pencil'];
let adjectives = ['purple', 'slimey', 'strange', 'huge'];
let adverbs = ['slowly'];
 
function getRandom(bank = ['word']) {
  return bank[Math.floor(Math.random() * bank.length)];
}
 
let madlib = `
  Once upon a ${nouns[0]} there was a ${adjectives[0]} ${nouns[1]}.
  Its ${nouns[2]} was ${adverbs[0]} turning ${adjectives[1]}.
  1 + 2 = ${1 + 2}. How ${getRandom(adjectives)}}...
`;
 
console.log(madlib);