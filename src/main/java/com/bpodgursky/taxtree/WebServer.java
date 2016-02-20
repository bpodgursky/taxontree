package com.bpodgursky.taxtree;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.bpodgursky.taxtree.servlet.DetailServlet;
import com.bpodgursky.taxtree.servlet.ExpandServlet;
import com.bpodgursky.taxtree.servlet.FindServlet;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebServer implements Runnable {

  private final Semaphore shutdownLock = new Semaphore(0);

  private final String userName;
  private final String password;
  private final String url;

  public WebServer(String userName, String password, String url) {
    this.userName = userName;
    this.password = password;
    this.url = url;
  }

  public final void shutdown() {
    shutdownLock.release();
  }

  @Override
  public void run() {
    try {

      Server uiServer = new Server(45321);
      uiServer.setThreadPool(new ExecutorThreadPool(20, 20, 15, TimeUnit.MINUTES));
      final URL warUrl = uiServer.getClass().getClassLoader().getResource("com/bpodgursky/taxtree/www");
      final String warUrlString = warUrl.toExternalForm();

      WebAppContext context = new WebAppContext(warUrlString, "/");

      QueryWrapper wrapper = new QueryWrapper(new PooledConnectionProvider(userName, password, url, "com.mysql.jdbc.Driver"));

      context.addServlet(new ServletHolder(new JSONServlet(new ExpandServlet(), wrapper)), "/expand_taxon");
      context.addServlet(new ServletHolder(new JSONServlet(new FindServlet(), wrapper)), "/find_taxon");
      context.addServlet(new ServletHolder(new JSONServlet(new DetailServlet(), wrapper)), "/detail_taxon");

      context.addFilter(GzipFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

      uiServer.setHandler(context);

      uiServer.start();

      shutdownLock.acquire();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws InterruptedException {
//    PropertyConfigurator.configure("com/bpodgursky/taxtree/log/log4j.properties");
    DOMConfigurator.configure(WebServer.class.getResource("/com/bpodgursky/taxtree/log/log4j.xml"));

    String userName = "root";
    String password = "";
    String url = "jdbc:mysql://localhost:3306/col2015ac";

    WebServer server = new WebServer(userName, password, url);
    Thread thread1 = new Thread(server);

    thread1.start();
    thread1.join();
  }
}
