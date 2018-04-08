export default {
  name: 'chart-3d',
  template: "<div style='width:500px; height:500px; border: 1px solid silver'></div>",
  props: [
    'options',
    'data'
  ],
  mounted: function(){
    this.drawChart()
  },
  watch: {
    options: function(opt){
      this.drawChart()
    },
    data: function(data){
      this.drawChart()
    }
  },
  methods: {
    drawChart: function(){
      this.graph3d = new vis.Graph3d(this.$el, this.data, this.options);
    }
  }
}