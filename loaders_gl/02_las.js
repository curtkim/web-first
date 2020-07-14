const fetch = require('node-fetch');
import {LASLoader} from '@loaders.gl/las';
import {parse} from '@loaders.gl/core';
//import {registerLoaders} from '@loaders.gl/core';

const LAZ_SAMPLE =
  'https://raw.githubusercontent.com/uber-common/deck.gl-data/master/examples/point-cloud-laz/indoor.0.1.laz';

async function main(){
    const data = await parse(fetch(LAZ_SAMPLE), LASLoader, {onProgress: function(a, b, c){console.log(a.progress)}});
    console.log(data);
}


main();
