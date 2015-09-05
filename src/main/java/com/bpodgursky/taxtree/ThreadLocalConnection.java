package com.bpodgursky.taxtree;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ThreadLocalConnection extends ThreadLocal<QueryWrapper> {

  private final String url;
  private final String userName;
  private final String password;

  public ThreadLocalConnection(String url, String username, String password) {
    this.url = url;
    this.userName = username;
    this.password = password;
  }

  @Override
  protected synchronized QueryWrapper initialValue() {
    try {
      return new QueryWrapper(DriverManager.getConnection(url, userName, password));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
