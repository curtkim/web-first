import {wasmBrowserInstantiate} from './wasm-utils'

async function runWasmAdd() {
  // Instantiate our wasm module
  const wasmModule = await wasmBrowserInstantiate("./hello-world.wasm");

  // Call the Add function export from wasm, save the result
  const addResult = wasmModule.instance.exports.add(24, 24);

  console.log(`Hello World! addResult: ${addResult}`);
}

runWasmAdd();