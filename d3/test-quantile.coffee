d3 = require 'd3'

#a = [0, 10, 30]
#console.log d3.quantile(a, 0)    # 0
#console.log d3.quantile(a, 0.5)  # 10
#console.log d3.quantile(a, 1)    # 30
#console.log d3.quantile(a, 0.25) # 5
#console.log d3.quantile(a, 0.75) # 20
#console.log d3.quantile(a, 0.1)  # 2


data = d3.range(100).map(d3.randomBates(5))
data.sort()
console.log data

console.log d3.quantile(data, 0)
console.log d3.quantile(data, 0.1)
console.log d3.quantile(data, 0.5)
console.log d3.quantile(data, 0.75)
console.log d3.quantile(data, 1)