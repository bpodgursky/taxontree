package com.bpodgursky.taxtree;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

public class PooledConnectionProvider implements ConnectionProvider {

  private final BasicDataSource connectionPool;

  public PooledConnectionProvider(String userName, String password, String url, String driver){
    connectionPool = new BasicDataSource();
    connectionPool.setUsername(userName);
    connectionPool.setPassword(password);
    connectionPool.setDriverClassName(driver);
    connectionPool.setUrl(url);
    connectionPool.setInitialSize(10);
    connectionPool.setMaxConnLifetimeMillis(60*60*1000);
  }

  @Override
  public Connection acquire() throws DataAccessException {
    try {
      return connectionPool.getConnection();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to open connection", e);
    }
  }

  @Override
  public void release(Connection connection) throws DataAccessException {
    try {
      connection.close();
    } catch (SQLException e) {
      throw new DataAccessException("Failed closing connection!", e);
    }
  }
}
