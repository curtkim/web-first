const fs = require('fs');
import {PCDLoader} from '@loaders.gl/pcd';
import {parse} from '@loaders.gl/core';
//import {registerLoaders} from '@loaders.gl/core';

async function main(){
    const data = await parse(fs.readFileSync('lidar.pcd'), PCDLoader, {onProgress: function(a){console.log(a.progress)}});
    console.log(data);
}

main();
