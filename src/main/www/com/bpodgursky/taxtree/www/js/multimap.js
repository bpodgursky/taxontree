function MultiMap(){
  this.kv = {};
}

MultiMap.prototype.keySet = function(){
  return Object.keys(this.kv);
};

MultiMap.prototype.entrySet = function(){
  var map = this;

  var edges = [];
  Object.keys(this.kv).forEach(function(key){
    Object.keys(map.kv[key]).forEach(function(value){
      edges.push({
        key: key,
        value: value
      })
    });
  });

  return edges;
};

MultiMap.prototype.put = function(k, v){
  if(!this.kv[k]){
    this.kv[k] = {};
  }

  this.kv[k][v] = true;
};

MultiMap.prototype.get = function(k){
  if(!this.kv[k]){
    return [];
  }
  return Object.keys(this.kv[k]);
};

MultiMap.prototype.removeAll = function(k){
  delete this.kv[k]
};

MultiMap.prototype.remove = function(k, v){
  if(this.kv[k]){
    delete this.kv[k][v];
  }
};