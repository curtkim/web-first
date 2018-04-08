tree = d3.layout.tree()

root =
  name: "1"
  children: [
    {name: "1-1"}
    {name: "1-2"}
    {name: "1-3"}
  ]

nodes = tree.nodes(root)
nodes.forEach((n)-> console.log n)
tree.links(nodes).forEach((l)-> console.log l)

###
[
        {name: "1", depth: 0, x: 0.5, y: 0},
        {name: "1-1", depth: 1, x: 0.16666666666666666, y: 1},
        {name: "1-2", depth: 1, x: 0.5, y: 1},
        {name: "1-3", depth: 1, x: 0.8333333333333333, y: 1}
      ]);
    },
###