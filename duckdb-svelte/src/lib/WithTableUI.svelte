<script lang="ts">
  import {initDB} from "../duckdb";
  import {Table} from "apache-arrow";
  import {onMount} from "svelte";
  import SvelteTable from "svelte-table";

  async function load_db() {
    // A simple case of a db of a single parquet file.
    const db = await initDB();
    await db.registerFileURL('SOTU.parquet', `/SOTU.parquet`, 4, false);
    return await db.connect();
  }

  const conn_prom = load_db();
  let years = []
  let year = 1936;

  const columns = [
    {
      key: "president",
      title: "President",
      value: v => v.president,
    },
    {
      key: "year",
      title: "Year",
      value: v => v.year,
    },
    {
      key: "paragraph",
      title: "Paragraph",
      value: v => v.paragraph,
    },
    {
      key: "nc:id",
      title: "nc:id",
      value: v => v['nc:id']
    },
    {
      key: "nc:text",
      title: "nc:text",
      value: v => v['nc:text']
    },
  ]

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
    <SvelteTable columns="{columns}" rows="{table.toArray()}"></SvelteTable>
  {:catch err}
    ERROR
  {/await}

</main>

<style>

</style>
