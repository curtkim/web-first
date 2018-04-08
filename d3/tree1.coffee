treeData = [{
  name: 'Top Level'
  parent: 'null'
  children: [
    {
      name: 'Level 2: A'
      parent: 'Top Level'
      children: [
        {
          name: 'Son of A'
          parent: 'Level 2: A'
        }
        {
          name: 'Daughter of A'
          parent: 'Level 2: A'
        }
      ]
    }
    {
      name: 'Level 2: B'
      parent: 'Top Level'
    }
  ]
}]


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
    # Normalize for fixed-depth.
    nodes.forEach (d) -> d.y = d.depth * 180
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

  root = treeData[0]
  update(root)