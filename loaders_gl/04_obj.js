const fs = require('fs');
import {OBJLoader} from '@loaders.gl/obj';
import {parse} from '@loaders.gl/core';
//import {registerLoaders} from '@loaders.gl/core';

async function main(){
    const data = await parse(fs.readFileSync('000001_points.obj'), OBJLoader, {onProgress: function(a){console.log(a.progress)}});
    console.log(data);
    console.log(data.header.boundingBox);
}

main();
