import { tableFromIPC } from "apache-arrow";
import {readFile} from 'fs/promises'

async function test() {
  const table = await tableFromIPC(readFile("../random_access_file.arrow"));
  console.table([...table]);
}

test()