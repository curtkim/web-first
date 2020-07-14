//const fetch = require('node-fetch');
import {readFileSync} from 'fs';
import {parse} from '@loaders.gl/core';
import {registerLoaders} from '@loaders.gl/core';
import {CSVLoader} from '@loaders.gl/csv';

async function main(){
    const data = await parse(readFileSync('./data.csv'), CSVLoader);
    console.log(data);
}

main();
