function GraphDiff(newNodes, removedNodes, expandedNodes, collapsedNodes, newEdges, removedEdges, selected, unselected) {
  this.newNodes = newNodes;
  this.removedNodes = removedNodes;
  this.expandedNodes = expandedNodes;
  this.collapsedNodes = collapsedNodes;
  this.newEdges = newEdges;
  this.removedEdges = removedEdges;
  this.selected = selected;
  this.unselected = unselected;
}

GraphDiff.prototype.absorb = function (otherDiff) {
  var diff = this;

  Object.keys(otherDiff.newNodes).forEach(function (e) {
    diff.newNodes[e] = otherDiff.newNodes[e];
  });

  Object.keys(otherDiff.removedNodes).forEach(function (e) {
    diff.removedNodes[e] = otherDiff.removedNodes[e];
  });

  Object.keys(otherDiff.collapsedNodes).forEach(function (e) {
    diff.collapsedNodes[e] = otherDiff.collapsedNodes[e];
  });

  Object.keys(otherDiff.expandedNodes).forEach(function (e) {
    diff.expandedNodes[e] = otherDiff.expandedNodes[e];
  });

  otherDiff.newEdges.forEach(function (e) {
    diff.newEdges.push(e);
  });

  otherDiff.removedEdges.forEach(function (e) {
    diff.removedEdges.push(e);
  });

  diff.selected = otherDiff.selected;
  diff.unselected = otherDiff.unselected;

  return this;
};

GraphDiff.prototype.expand = function (id, node) {
  this.expandedNodes[id] = node;
  return this;
};

GraphDiff.prototype.collapse = function (id, node) {
  this.collapsedNodes[id] = node;
  return this;
};

GraphDiff.prototype.addNode = function (id, node) {
  this.newNodes[id] = node;
  return this;
};

GraphDiff.prototype.removeNode = function (id, node) {
  this.removedNodes[id] = node;
  return this;
};

GraphDiff.prototype.select = function (select, unselect) {
  this.selected = select;
  this.unselected = unselect;
  return this;
};

GraphDiff.prototype.addEdge = function (source, target) {
  this.newEdges.push({
    source: source,
    target: target
  });
  return this;
};

GraphDiff.noDiff = function () {
  return new GraphDiff({}, {}, {}, {}, [], [], null, null);
};

function BackedBiGraph(fetch, find, detail, idGen, onModify) {
  this.fetch = fetch;
  this.find = find;
  this.detail = detail;
  this.idGen = idGen;
  this.nodes = {};
  this.expanded = {};
  this.selected = null;
  this.detail = null;
  this.edges = new BiMap();
  this.onModify = onModify;
}

BackedBiGraph.prototype.node = function (id) {
  return this.nodes[id];
};

BackedBiGraph.prototype.edgeSet = function () {
  var edges = [];

  this.edges.entrySet().forEach(function (e) {
    edges.push({
      source: e.key,
      target: e.value
    })
  });

  return edges;
};

BackedBiGraph.prototype.select = function (id) {
  var graph = this;

  //  only select after inserting
  if(!this.nodes[id]){
    return;
  }

  //  no change
  if (this.selected == id) {
    return;
  }

  this.find(id, function (node) {
    var unselect = graph.selected;
    graph.selected = id;
    graph.detail = node;
    graph.onModify(GraphDiff.noDiff().select(id, unselect));
  });

};

BackedBiGraph.prototype.insert = function (id) {

  //  it's already in the graph
  if (this.nodes[id]) {
    return;
  }

  this.find(id, function (node) {
    graph.onModify(graph._insertNode(id, node));
  });

};

BackedBiGraph.prototype.toggleExpand = function (id) {
  if (this.expanded[id]) {
    this.collapse(id);
  } else {
    this.expand(id);
  }
};

BackedBiGraph.prototype.expand = function (id) {
  var graph = this;

  var node = this.nodes[id];
  if (!node) {
    throw "Cannot exand a node which is not in the graph!";
  }

  //  we know there's no work to do there
  if (this.expanded[id]) {
    return;
  }

  this.fetch(id, function (parents, children) {
    var diff = GraphDiff.noDiff().expand(id, node);

    parents.forEach(function (e) {
      var parentId = graph.idGen(e);

      diff.absorb(
          graph._insertNode(parentId, e)
      );

      diff.absorb(
          graph._insertEdge(parentId, id)
      );

    });

    children.forEach(function (e) {
      var childId = graph.idGen(e);

      diff.absorb(
          graph._insertNode(childId, e)
      );

      diff.absorb(
          graph._insertEdge(id, childId)
      );

    });

    graph.expanded[id] = true;
    graph.onModify(diff);
  });

};

BackedBiGraph.prototype.collapse = function (id) {
  var graph = this;

  var node = this.nodes[id];
  if (!node) {
    //  nothing to do
    return;
  }

  var diff = GraphDiff.noDiff().collapse(id, node);

  this.gatherChildren(id).forEach(function (e) {
    diff.absorb(graph._remove(e));
  });

  delete graph.expanded[id];

  graph.onModify(diff);
};

BackedBiGraph.prototype.gatherChildren = function (id) {

  var descendants = {};
  var queue = [id];

  while (queue.length != 0) {
    var next = queue.pop();
    this.edges.getKey(next).forEach(function (child) {
      if (!descendants[child]) {
        descendants[child] = true;
        queue.push(child);
      }
    });
  }
  return Object.keys(descendants);
};

BackedBiGraph.prototype.remove = function (id) {
  this.onModify(this._remove(id));
};

//  private

BackedBiGraph.prototype._insertNode = function (id, node) {

  if (graph.nodes[id]) {
    return GraphDiff.noDiff();
  }

  graph.nodes[id] = node;
  return GraphDiff.noDiff().addNode(id, node);

};

BackedBiGraph.prototype._insertEdge = function (id, child) {

  if (graph.edges.getKey(id)[child]) {
    return GraphDiff.noDiff();
  }

  graph.edges.put(id, child);
  return new GraphDiff.noDiff().addEdge(id, child);
};

BackedBiGraph.prototype._remove = function (id) {
  var graph = this;

  var node = this.nodes[id];
  if (!node) {
    //  nothing to do
    return GraphDiff.noDiff();
  }

  var removedEdges = [];
  var collapsedNodes = {};

  this.edges.getKey(id).forEach(function (e) {
    removedEdges.push({
      source: id,
      target: e
    });
  });

  this.edges.getVal(id).forEach(function (e) {
    removedEdges.push({
      source: e,
      target: id
    })
  });


  if (graph.expanded[id]) {
    collapsedNodes[id] = graph.nodes[id];
  }

  this.edges.getKey(id).forEach(function (e) {
    if (graph.nodes[e]) {
      if (graph.expanded[e]) {
        collapsedNodes[e] = graph.nodes[e];
        delete graph.expanded[e];
      }
    }
  });

  this.edges.getVal(id).forEach(function (e) {
    if (graph.nodes[e]) {
      if (graph.expanded[e]) {
        collapsedNodes[id] = graph.nodes[e];
        delete graph.expanded[e];
      }
    }
  });

  this.edges.removeAll(id);

  var unselect = null;
  if (this.selected == id) {
    this.selected = null;
    unselect = id;
  }

  delete this.nodes[id];
  delete this.expanded[id];

  return new GraphDiff({}, obj(id, node), {}, collapsedNodes, [], [removedEdges], null, unselect);
};


function obj(id, node) {
  var obj = {};
  obj[id] = node;
  return obj;
}
