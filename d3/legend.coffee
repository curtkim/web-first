test1 = ()->
  quantize = d3.scale.quantize()
    .domain([ 0, 0.15 ])
    .range(d3.range(9).map( (i)-> "q" + i + "-9" ))

  svg = d3.select("#svg-color-quant")

  svg.append("g")
    .attr("class", "legendQuant")
    .attr("transform", "translate(20,20)")

  legend = d3.legend.color()
    .labelFormat(d3.format(".2f"))
    .useClass(true)
    .scale(quantize)

  svg.select(".legendQuant").call(legend)


test2 = ()->
  linear = d3.scale.linear()
    .domain([0,10])
    .range(["rgb(46, 73, 123)", "rgb(71, 187, 94)"])

  svg = d3.select("#svg-color-linear-10")

  svg.append("g")
    .attr("class", "legendLinear")
    .attr("transform", "translate(20,20)")

  legendLinear = d3.legend.color()
    .shapeWidth(30)
    .cells(11)
    .orient('horizontal')
    .scale(linear)

  svg.select(".legendLinear").call(legendLinear)


$(document).ready ()->
  test1()
  test2()

