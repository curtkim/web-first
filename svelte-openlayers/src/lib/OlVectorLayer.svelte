<script lang="ts">

  import {getContext, onMount, onDestroy} from 'svelte';
  import {Map } from 'ol';
  import VectorLayer from 'ol/layer/Vector';
  import VectorSource from 'ol/source/Vector';
  import {key} from './ol-common.ts'
  import type {StyleLike} from "ol/style/Style";
  import Feature from "ol/Feature";

  export let style : StyleLike;
  export let features: Array<Feature> = [];

	const { getMap } = getContext(key);
	const map:Map = getMap();

  const vectorLayer = new VectorLayer({
    source: new VectorSource({ features }),
    style,
  });

  onMount(() => {
    map.addLayer(vectorLayer);
  });
  onDestroy(() => {
    map.removeLayer(vectorLayer);
  });

  let prevFeatures = [];
  $: {
    console.log(features.length, prevFeatures.length)
    prevFeatures = features;
    vectorLayer.getSource().clear();
    vectorLayer.getSource().addFeatures(features);
  }
</script>

<div></div>

<style>
</style>
