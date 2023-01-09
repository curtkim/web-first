import { tableFromIPC } from "apache-arrow";
import {
  readParquet,
} from "parquet-wasm/node/arrow2";
import {readFile} from 'fs/promises'

async function mytest() {
  const response = await readFile('example.parquet');
  const parquetUint8Array = new Uint8Array(response);
  const arrowUint8Array = readParquet(parquetUint8Array);
  const table = tableFromIPC(arrowUint8Array);
  console.log(table.schema.toString());

  console.table(table.toArray(), ['pop_est', 'continent', 'name', 'iso_a3', 'gdp_md_est'])

  const geometryColumn = table.getChild("geometry")
  console.log(geometryColumn.get(0))
}

mytest()