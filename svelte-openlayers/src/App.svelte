<script lang="ts">
  import OlMap from "./lib/OlMap.svelte"
  import OlVectorLayer from "./lib/OlVectorLayer.svelte"
  import {Fill, RegularShape, Stroke, Style} from 'ol/style';
  import Feature from 'ol/Feature';
  import Point from 'ol/geom/Point';

  const stroke = new Stroke({color: 'black', width: 2});
  const fill = new Fill({color: 'red'});
  const triangle = new Style({
    image: new RegularShape({
      fill: fill,
      stroke: stroke,
      points: 3,
      radius: 10,
      rotation: 0, //Math.PI / 4,
      angle: 0,
    }),
  })

  let features = [
    new Feature({
      geometry: new Point([204312, 421282]),
    }),
    new Feature({
      geometry: new Point([208803, 424986]),
    }),
  ];

  function addFeature(){
    features = [...features, new Feature({
      geometry: new Point([195063,442898]),
    })];
  }

</script>

<button on:click={addFeature}>add feature</button>
<div class="mapContainer">
  <OlMap center={[195063,442898]} level={10}>
    <OlVectorLayer features={features} style={triangle}/>
  </OlMap>
</div>

<style>
  .mapContainer {
    position: absolute;
    width: 80%;
    height: 100%;
    left: 20%;
    background-color: #eee;
  }
</style>
