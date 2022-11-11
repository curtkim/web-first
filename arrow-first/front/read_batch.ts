import {readFile} from 'fs/promises'
import { Table, RecordBatchReader } from 'apache-arrow';

async function loadData() {
  const response = await readFile('../test_dict_stream.arrow');
  const reader = await RecordBatchReader.from(response);
  await reader.open();
  let table = new Table(reader.schema);
  for await (const recordBatch of reader) {
    table.batches.push(recordBatch)
    //console.log(recordBatch.get(0))
    //table = table.concat(recordBatch);
  }
  console.table(table.toArray())
};

loadData()
