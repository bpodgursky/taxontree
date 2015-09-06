package com.bpodgursky.taxtree.servlet;

import java.util.Collection;
import java.util.Map;

import com.bpodgursky.taxtree.JSONServlet;
import com.bpodgursky.taxtree.QueryWrapper;
import com.bpodgursky.taxtree.TaxonInfo;

public class ExpandServlet implements JSONServlet.Processor {

  private static class GraphResponse {
    private final Collection<TaxonInfo> children;
    private final Collection<TaxonInfo> parents;

    private GraphResponse(Collection<TaxonInfo> children, Collection<TaxonInfo> parents) {
      this.children = children;
      this.parents = parents;
    }
  }

  @Override
  public Object getData(QueryWrapper query, Map<String, String> parameters) throws Exception {
    Long taxonId = Long.parseLong(parameters.get("taxon_id"));

    return new GraphResponse(
        query.children(taxonId),
        query.parents(taxonId)
    );
  }

}
