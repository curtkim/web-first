<script lang="ts">
  import 'ol/ol.css';

  import {setContext, onMount} from 'svelte';
  import {Map, View} from 'ol';
  import TileLayer from 'ol/layer/Tile';
  import {key} from './ol-common.ts'

  import {DAUM_ROAD_MAP} from '../ol-daum.js'

  export let center: [number, number];
  export let level: number;

  function level2zoom(level){
    return 14-level + 3
  }

  let container;
  let map: Map;

  onMount(()=>{
    map = new Map({
      target: container,
      layers: [
        new TileLayer({
          source: DAUM_ROAD_MAP,
        }),
      ],
      view: new View({
        center: center,
        zoom: level2zoom(level)
      }),
    });
  });

  setContext(key, {
    getMap : ()=> map
  })

</script>

<div bind:this={container} style="width: 100%; height: 100%">
  {#if map}
    <slot/>
  {/if}
</div>

<style>
</style>
