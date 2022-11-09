import './style.css'

//import { tableFromIPC } from 'apache-arrow';
// import * as Arrow from 'apache-arrow';

//const table = tableFromIPC(arrow);
//console.table(table.toArray());

// const data = {
//   table: new Arrow.Table()
// };

// const loadData = async () => {
//   const response = await fetch('/random_access_file.arrow');
//   const reader = await Arrow.RecordBatchReader.from(response);
//   await reader.open();
//   data.table = new Arrow.Table(reader.schema);
//   for await (const recordBatch of reader) {
//     data.table = data.table.concat(recordBatch);
//     document.querySelector('#loading>span').innerHTML =
//       new Intl.NumberFormat().format(data.table.length) + ' points loaded';
//     redraw();
//   }
// };
