package com.bpodgursky.taxtree.servlet;

import java.util.Map;

import com.bpodgursky.taxtree.JSONServlet;
import com.bpodgursky.taxtree.QueryWrapper;

public class FindServlet implements JSONServlet.Processor{
  @Override
  public Object getData(QueryWrapper query, Map<String, String> parameters) throws Exception {
    return query.id(Long.parseLong(parameters.get("taxon_id")));
  }
}
