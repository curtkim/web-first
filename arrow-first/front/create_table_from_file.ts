import { readFileSync } from 'fs';
import { tableFromIPC } from 'apache-arrow';

//const arrow = readFileSync('../random_access_file.arrow');
const arrow = readFileSync('../test_dict_stream.arrow');
const table = tableFromIPC(arrow);

console.table(table.toArray());

console.log(table.schema.fields)
//for(const item of table)
//  console.log(item)