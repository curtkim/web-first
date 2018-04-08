data = [{
  7: 1
  8: 1
  9: 3
  10: 4
  11: 4
  12: 6
}
{
  13: 7
  14: 7
  15: 9
  16: 11
  17: 12
  18: 12
}]

root =
  name: 'root'
  parent: null
  children: []

nodeMap = {}
for t in [0..2]
  for c in [0..5]
    name = t*6+c+1
    nodeMap[name] = {name, parent: null, children:[]}

for t in [1..0]
  for t, s of data[t]
    nodeMap[t].parent = nodeMap[s]
    nodeMap[s].children.push nodeMap[t]

for i in [1..6]
  nodeMap[i].parent = root
  root.children.push nodeMap[i]


$(document).ready ()->
  margin =
    top: 20
    right: 120
    bottom: 20
    left: 120
  width = 960 - (margin.right) - (margin.left)
  height = 500 - (margin.top) - (margin.bottom)
  i = 0
  tree = d3.layout.tree().size([ height, width ])
  diagonal = d3.svg.diagonal().projection((d) -> [ d.y, d.x ])
  svg = d3.select('body').append('svg')
    .attr('width', width + margin.right + margin.left)
    .attr('height', height + margin.top + margin.bottom)
    .append('g')
      .attr('transform', "translate(#{margin.left},#{margin.top})")

  update = (source) ->
    # Compute the new tree layout.
    nodes = tree.nodes(root).reverse()
    links = tree.links(nodes)
    for n in nodes when n.name != 'root'
      t = parseInt (n.name-1) / 6
      j = (n.name-1) % 6
      n.x = (j+1)*50


    # Normalize for fixed-depth.
    nodes.forEach (d) -> d.y = d.depth * 100
    # Declare the nodes
    node = svg.selectAll('g.node').data(nodes, (d) -> d.id or (d.id = ++i))
    # Enter the nodes.
    nodeEnter = node.enter().append('g')
      .attr('class', 'node')
      .attr('transform', (d) -> "translate(#{d.y},#{d.x})" )
    nodeEnter.append('circle')
      .attr('r', 10)
      .style('fill', '#fff')
    nodeEnter.append('text')
      .attr('x', (d) -> if d.children or d._children then -13 else 13)
      .attr('dy', '.35em')
      .attr('text-anchor', (d) -> if d.children or d._children then 'end' else 'start')
      .text((d) -> d.name)
      .style('fill-opacity', 1)

    # Declare the links
    link = svg.selectAll('path.link').data(links, (d) -> d.target.id)
    # Enter the links.
    link.enter().insert('path', 'g')
      .attr('class', 'link')
      .attr('d', diagonal)

  update(root)