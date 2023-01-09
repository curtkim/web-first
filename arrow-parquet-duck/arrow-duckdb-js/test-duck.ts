//import { tableFromArrays } from 'apache-arrow';
import { writeFileSync} from 'fs'

import { tableFromIPC } from 'apache-arrow';
import duckdb from 'duckdb'
const db = new duckdb.Database(':memory:');
const conn = db.connect();

const query = "SELECT traffic_id, count(*) as cnt from read_parquet('navi-*.parquet') group by traffic_id";

conn.all(query, function (err, res){
  if(err)
    console.log(err)
  else
    console.log(res[0])
});

conn.exec("install arrow; load arrow;")

conn.arrowIPCAll(query, function(err, res) {
  if (err) {
    throw err;
  }

  console.table([res]);

  const table = tableFromIPC(res);
  console.log(table.schema)
  //console.table(table.toArray())

  writeFileSync("temp.arrow", res[0])
  //console.log(typeof res) // object
  //console.log(res.toString())
  //console.log(res[0])
});

