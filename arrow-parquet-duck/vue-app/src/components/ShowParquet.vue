<script setup lang="ts">
// 실패함 readParquet에서 에러 발생
import {ref, onMounted, Ref} from 'vue'
import { tableFromIPC, Table } from "apache-arrow";
import {readParquet} from "parquet-wasm/esm/arrow2"

const props = defineProps<{ parquetUrl: string }>()

const table: Ref<Table<any>|null> = ref(null)

onMounted(async () => {
  table.value = await loadParquet(props.parquetUrl)
})

async function loadParquet(parquetUrl : String): Promise<Table<any>> {
  const response = await fetch(parquetUrl);
  const parquetUint8Array = new Uint8Array(await response.arrayBuffer());
  const arrowUint8Array = readParquet(parquetUint8Array);
  return tableFromIPC(arrowUint8Array);
}

</script>

<template>
  <div>{{ table }}</div>
</template>

<style scoped>
</style>
