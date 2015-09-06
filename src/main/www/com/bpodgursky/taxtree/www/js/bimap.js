
function BiMap(){
  this.kv = new MultiMap();
  this.vk = new MultiMap();
}

BiMap.prototype.keySet = function(){
  return this.kv.keySet();
};

BiMap.prototype.entrySet = function(){
  return this.kv.entrySet();
};

BiMap.prototype.getKey = function(k){
  return this.kv.get(k);
};

BiMap.prototype.getVal = function(k){
  return this.vk.get(k);
};

BiMap.prototype.put = function(k, v){
  this.kv.put(k, v);
  this.vk.put(v, k);
};

BiMap.prototype.removeAll = function(k){
  var graph = this;

  this.kv.get(k).forEach(function(e){
    graph.vk.removeAll(e);
  });

  this.kv.removeAll(k);

};