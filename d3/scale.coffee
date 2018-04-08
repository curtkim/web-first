# http://www.jeromecukier.net/blog/2011/08/11/d3-scales-and-color/
$(document).ready ()->
  y= d3.scale.linear().domain([20,80]).range([0,120])
  console.log y(30)
  console.log y(20)
  console.log y(80)
  console.log y(90)

  ramp= d3.scale.linear().domain([0,100]).range(["red","blue"])
  console.log ramp(50)

  clamp= d3.scale.linear().domain([20,80]).range([0,120])
  console.log clamp(100) # 160
  clamp.clamp(true) # clamp = 단속하다, 잠그다
  console.log clamp(100) # 120


  data=[-2.347, 4, 5.23, -1.234, 6.234, 7.431] # or whatever.
  y= d3.scale.linear().range([0,120])
  y.domain([d3.min(data), d3.max(data)]) # domain takes bounds as arguments, not all numbers
  console.log y.domain() # [-2.347, 7.431]
  console.log y.nice() # [-3, 8]
  #console.log y.rangeRound()

  q= d3.scale.quantize().domain([0,10]).range([0,2,8])
  console.log q(0)    # 0
  console.log q(3)    # 0
  console.log q(3.33) # 0
  console.log q(3.34) # 2
  console.log q(5)    # 2
  console.log q(6.66) # 2
  console.log q(6.67) # 8
  console.log q(8)    # 8
  console.log q(1000) # 8

  c= d3.rgb("violet")      # d3_Rgb object
  c.toString()                # "#ee82ee"
  c.darker().toString()       # "#a65ba6"
  c.darker(2).toString()      # "#743f74" - even darker
  c.brighter().toString()     # "ffb9ff"
  c.brighter(0.1).toString()  # "#f686f6" - only slightly brighter
  c.hsl()                     # d3_Hsl object
  c.hsl().toString()          # hsl(300, 76, 72)"

  domain = [0, 1, 10, 100, 1000, 10000, 100000, 1000000]
  quantile = d3.scale.quantile()
    .domain(domain)
    .range([0,10])
  console.log('quantile', quantile(10))     #0
  console.log('quantile', quantile(100))    #0
  console.log('quantile', quantile(1000))   #10
  console.log('quantile', quantile(10000))  #10