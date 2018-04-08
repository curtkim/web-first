import DaumImg from './ol-daum'

export default {
  template: "<div style='width:100%; height:500px;'></div>",
  props: [
    'level',
    'center'
  ],
  mounted: function(){
    var daumImgLayer = new ol.layer.Tile({
      source: new DaumImg()
    })

    this.map = new ol.Map({
      target: this.$el,
      layers: [
        daumImgLayer
      ],
      view: new ol.View({
        center: this.center,
        zoom: 14 - this.level,
        maxZoom: 14,
        maxResolution: 2048 // 14level(zoom=0)에서 1px은 2048m와 같다
      }),
      controls: [
        new ol.control.Zoom(),
        new ol.control.MousePosition()
      ]
    })
  },
  watch: {
    level: function(val){
      this.map.getView().setZoom(14-val)  
    }
  }
}