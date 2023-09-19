<script lang="ts">
  import {initDB} from "../duckdb";
  import {Table} from "apache-arrow";
  import {onMount} from "svelte";

  async function load_db() {
    // A simple case of a db of a single parquet file.
    const db = await initDB();
    await db.registerFileURL('SOTU.parquet', `/SOTU.parquet`, 4, false);
    return await db.connect();
  }

  const conn_prom = load_db();
  let years = []
  let year = 1936;

  console.log("init")
  onMount(async () => {
    console.log("onMount")
    years = await get_year_list();
  })

  async function get_year_list(): Promise<Array<number>> {
    const conn = await conn_prom;
    const t = await conn.query(`SELECT distinct year FROM 'SOTU.parquet'`)
    return t.toArray().map(row => row.year)
  }

  async function get_year(y) : Promise<Table<any>> {
    const conn = await conn_prom;
    return conn.query(`SELECT *
                             FROM 'SOTU.parquet'
                             WHERE year == ${y} limit 5`);
  }

  $: table = get_year(year)
</script>

<main>
  <form>
    <label>year : </label>
    <select bind:value={year}>
      {#each years as y}
        <option value="{y}">{y}</option>
      {/each}
    </select>
  </form>

  <hr/>

  {#await table}
    ...
  {:then table}
    <table>
      <thead>
      <tr>
        <th>president</th>
        <th>year</th>
        <th>party</th>
        <th>para</th>
        <th>nc:id</th>
        <th>nc:text</th>
      </tr>
      </thead>
      {#each table.toArray() as row}
        <tr>
          <td>{row.president}</td>
          <td>{row.year}</td>
          <td>{row.party}</td>
          <td>{row.paragraph}</td>
          <td>{row['nc:id']}</td>
          <td>{row['nc:text']}</td>
        </tr>
      {/each}
    </table>
    <!--
    <hr/>
    <ul>
      {#each table.schema.fields as field}
        <li>{field.name} {field.type} {field.nullable} {field.metadata}</li>
      {/each}
    </ul>
    -->
  {:catch err}
    ERROR
  {/await}

</main>

<style>

</style>
