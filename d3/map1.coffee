$(document).ready ()->

  w = 1280
  h = 800

  projection = d3.geo.azimuthal()
      .mode("equidistant")
      .origin([-98, 38])
      .scale(1400)
      .translate([640, 360])

  path = d3.geo.path().projection(projection)

  svg = d3.select("body").insert("svg:svg", "h2")
      .attr("width", w)
      .attr("height", h)

  #console.log(svg)

  states = svg.append("svg:g").attr("id", "states")
  circles = svg.append("svg:g").attr("id", "circles")
  cells = svg.append("svg:g").attr("id", "cells")

  d3.select("input[type=checkbox]").on "change", ()-> cells.classed("voronoi", this.checked)

  d3.json "us-states.json", (collection)->
    states.selectAll("path")
      .data(collection.features)
      .enter().append("svg:path")
        .attr("d", path)

  d3.csv "flights-airport.csv", (flights)->
    linksByOrigin = {}
    countByAirport = {}
    locationByAirport = {}
    positions = []

    arc = d3.geo.greatArc()
        .source((d)-> locationByAirport[d.source] )
        .target((d)-> locationByAirport[d.target] )

    for flight in flights
      origin = flight.origin
      destination = flight.destination
      links = linksByOrigin[origin] || (linksByOrigin[origin] = [])
      links.push({source: origin, target: destination})
      countByAirport[origin] = (countByAirport[origin] || 0) + 1
      countByAirport[destination] = (countByAirport[destination] || 0) + 1


    d3.csv "airports.csv", (airports)->

      # Only consider airports with at least one flight.
      airports = airports.filter (airport)->
        if (countByAirport[airport.iata])
          location = [+airport.longitude, +airport.latitude]
          locationByAirport[airport.iata] = location
          positions.push(projection(location))
          return true

      # Compute the Voronoi diagram of airports' projected positions.
      polygons = d3.geom.voronoi(positions)

      g = cells.selectAll("g")
          .data(airports)
          .enter().append("svg:g");

      g.append("svg:path")
          .attr("class", "cell")
          .attr("d", (d, i)-> return "M" + polygons[i].join("L") + "Z" )
          .on("mouseover", (d, i)-> d3.select("h2 span").text(d.name) )

      g.selectAll("path.arc")
          .data((d)-> return linksByOrigin[d.iata] || [] )
        .enter().append("svg:path")
          .attr("class", "arc")
          .attr("d", (d)-> return path(arc(d)) )

      circles.selectAll("circle")
          .data(airports)
        .enter().append("svg:circle")
          .attr("cx", (d, i)-> return positions[i][0] )
          .attr("cy", (d, i)-> return positions[i][1] )
          .attr("r", (d, i)-> return Math.sqrt(countByAirport[d.iata]) )
          .sort((a, b) -> return countByAirport[b.iata] - countByAirport[a.iata] )
