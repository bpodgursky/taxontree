package com.bpodgursky.taxtree;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONServlet extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(JSONServlet.class);
  private String ALLOWED_DOMAIN = "http://admin.liveramp.net";

  private static final Gson GSON = new Gson();

  private final ThreadLocal<QueryWrapper> queryWrapper;
  private final Processor processor;

  public JSONServlet(Processor processor, ThreadLocal<QueryWrapper> queryWrapper) {
    this.processor = processor;
    this.queryWrapper = queryWrapper;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    Map<String, String> entries = Maps.newHashMap();
    for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
      if (entry.getValue().length != 1) {
        throw new RuntimeException("Invalid # of values for param " + entry.getKey() + ": " + Arrays.toString(entry.getValue()));
      }
      entries.put(entry.getKey(), entry.getValue()[0]);
    }

    try {

      long start = System.currentTimeMillis();
      Object data = processor.getData(queryWrapper.get(), entries);

      LOG.info("Request " + getRequestURL(req) + " processed in " + (System.currentTimeMillis() - start) + "ms");
      String originHeader = req.getHeader("Origin");
      if (originHeader != null && originHeader.equals(ALLOWED_DOMAIN)) {
        resp.setHeader("Access-Control-Allow-Origin", ALLOWED_DOMAIN);
      }
      resp.getWriter().append(GSON.toJson(data));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getRequestURL(HttpServletRequest req) {
    return req.getRequestURL().toString() + "?" + queryString(req);
  }

  private String queryString(HttpServletRequest req) {
    if (req.getQueryString() == null) {
      return "";
    }
    return req.getQueryString();
  }

  public interface Processor {
    Object getData(QueryWrapper query, Map<String, String> parameters) throws Exception;
  }
}