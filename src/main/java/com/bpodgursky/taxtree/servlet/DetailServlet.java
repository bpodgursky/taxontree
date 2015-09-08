package com.bpodgursky.taxtree.servlet;

import java.util.Map;

import com.bpodgursky.taxtree.JSONServlet;
import com.bpodgursky.taxtree.QueryWrapper;

public class DetailServlet implements JSONServlet.Processor{
  @Override
  public Object getData(QueryWrapper query, Map<String, String> parameters) throws Exception {
    return query.detail(Long.parseLong(parameters.get("taxon_id")));
  }
}
