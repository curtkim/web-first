<script setup lang="ts">
import {initDB} from "../duckdb";
import {onMounted, ref, Ref} from "vue";
import {Table, tableFromArrays} from "apache-arrow";

async function load_db() {
  console.log("LOADING DB")
  // A simple case of a db of a single parquet file.
  const db = await initDB();
  await db.registerFileURL('SOTU.parquet', `/SOTU.parquet`, 4, false);
  const conn = await db.connect();
  /*
  console.log("PREPARING INSERT")
  await conn.query(`CREATE TABLE p1 AS
  SELECT *
  FROM parquet_scan('SOTU.parquet')`);
  await conn.query(`CREATE VIEW wordcounts_raw AS SELECT * FROM (SELECT "@id" id,
        UNNEST("nc:unigrams").word0 as word,
        UNNEST("nc:unigrams").count as count FROM
        p1) t1
    `);
  await conn.query(`
    CREATE TABLE wordcounts AS
    SELECT *
    FROM wordcounts_raw
           NATURAL JOIN (SELECT word, SUM(count) as tot, COUNT(*) AS df FROM wordcounts_raw GROUP BY word) t2
  `);
  */
  return conn;
}

const conn_prom = load_db();
const years = []
// We could get this from the database, but no need to show off.
for (let y = 1934; y < 2021; y++) {
  years.push(y)
}

const table: Ref<Table<any> | null> = ref(null)

async function get_year(y) {
  const conn = await conn_prom;
  table.value = await conn.query(`
    SELECT *
    FROM parquet_scan('SOTU.parquet')
    WHERE year == ${y}
  `)
  console.log(table.value?.toArray())
}

onMounted(() => {
  conn_prom.then(() => get_year(1936))
})

</script>

<template>
  <div v-if="table != null">
    <table>
      <tr v-for="row in table.toArray()">
        <td>{{ row.president }}</td>
        <td>{{ row.year }}</td>
        <td>{{ row.party }}</td>
        <td>{{ row.paragraph }}</td>
        <td>{{ row['nc:id'] }}</td>
        <td>{{ row['nc:text'] }}</td>
      </tr>
    </table>

    <ul v-for="f in table.schema.fields">
      <li><b>{{ f.name }}</b> <i>{{ f.type }}</i> {{ f.nullable }} {{ f.metadata }}</li>
    </ul>
  </div>

</template>

<style scoped>
table {
  border-collapse: collapse;
}

table td {
  border: 1px solid silver;
  padding: 5px;
}
</style>
