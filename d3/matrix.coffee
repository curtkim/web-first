setTimeout2 = (t, f)-> setTimeout f, t

$(document).ready ()->

  margin = {top: 100, right: 0, bottom: 0, left: 100}
  width = 1024
  height = 1024


  x = d3.scale.ordinal().rangeBands([0, width])
  z = d3.scale.linear().domain([0, 4]).clamp(true)
  c = d3.scale.category10().domain(d3.range(10))

  svg = d3.select("body").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .style("margin-left", margin.left + "px")
        .append("g")
        .attr("transform", "translate(#{margin.left},#{margin.top})")

  #console.log 'x.rangeBand()', x.rangeBand()

  d3.json "miserables.json", (miserables)->
    matrix = []
    nodes = miserables.nodes
    n = nodes.length

    # Compute index per node.
    for node, i in nodes
      node.index = i
      node.count = 0
      matrix[i] = d3.range(n).map (j)-> {x: j, y: i, z: 0}

    #console.log nodes

    # Convert links to matrix; count character occurrences.
    for link in miserables.links
      matrix[link.source][link.target].z += link.value
      matrix[link.target][link.source].z += link.value
      matrix[link.source][link.source].z += link.value
      matrix[link.target][link.target].z += link.value
      nodes[link.source].count += link.value
      nodes[link.target].count += link.value

    console.log matrix
    # Precompute the orders.
    orders =
      name: d3.range(n).sort (a, b)-> d3.ascending(nodes[a].name, nodes[b].name)
      count: d3.range(n).sort (a, b)-> nodes[b].count - nodes[a].count
      group: d3.range(n).sort (a, b)-> nodes[b].group - nodes[a].group


    # The default sort order.
    console.log 'orders.name', orders.name
    x.domain(orders.name)

    svg.append("rect")
        .attr("class", "background")
        .attr("width", width)
        .attr("height", height)

    fnRow = (row)->
      d3.select(this).selectAll(".cell")
          .data(row.filter((d)-> d.z ))
          .enter().append("rect")
            .attr("class", "cell")
            .attr("x", (d)-> x(d.x) )
            .attr("width", x.rangeBand())
            .attr("height", x.rangeBand())
            .style("fill-opacity", (d)-> z(d.z) )
            .style("fill", (d)-> if nodes[d.x].group == nodes[d.y].group then c(nodes[d.x].group) else null )
            .on("mouseover", mouseover)
            .on("mouseout", mouseout)

    mouseover = (p) ->
      d3.selectAll(".row text").classed "active", (d, i)-> i == p.y
      d3.selectAll(".column text").classed "active", (d, i)-> i == p.x

    mouseout = ()->
      d3.selectAll("text").classed("active", false)


    row = svg.selectAll(".row")
        .data(matrix)
        .enter().append("g")
          .attr("class", "row")
          .attr("transform", (d, i)-> "translate(0,#{x(i)})" )
          .each(fnRow)

    row.append("line")
      .attr("x2", width)

    row.append("text")
        .attr("x", -6)
        .attr("y", x.rangeBand() / 2)
        .attr("dy", ".32em")
        .attr("text-anchor", "end")
        .text((d, i) -> nodes[i].name )

    column = svg.selectAll(".column")
        .data(matrix)
        .enter().append("g")
          .attr("class", "column")
          .attr("transform", (d, i)-> "translate(#{x(i)}) rotate(-90)" )

    column.append("line")
      .attr("x1", -width)

    column.append("text")
        .attr("x", 6)
        .attr("y", x.rangeBand() / 2)
        .attr("dy", ".32em")
        .attr("text-anchor", "start")
        .text (d, i)-> nodes[i].name

    #console.log 'x.rangeBand()', x.rangeBand()

    order = (value)->
      x.domain(orders[value])

      t = svg.transition().duration(2500)

      t.selectAll(".row")
          .delay((d, i)-> x(i) * 4)
          .attr("transform", (d, i)-> "translate(0,#{x(i)})")
        .selectAll(".cell")
          .delay((d)-> x(d.x) * 4)
          .attr("x", (d)-> x(d.x) )

      t.selectAll(".column")
          .delay((d, i)-> x(i) * 4)
          .attr("transform", (d, i)-> "translate(#{x(i)})rotate(-90)" )

    d3.select("#order").on "change", ()->
      #clearTimeout(timeout)
      order(this.value)

    setTimeout2 1000, ()->

    #timeout = setTimeout2 5000, ()->
    #  order("group")
      #d3.select("#order").property("selectedIndex", 2).node().focus()
