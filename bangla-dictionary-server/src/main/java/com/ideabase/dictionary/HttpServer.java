/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */

package com.ideabase.dictionary;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.jasper.servlet.JspServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.thread.BoundedThreadPool;
import org.mortbay.thread.ThreadPool;
import com.ideabase.dictionary.servlet.DictionaryServiceServlet;
import com.ideabase.dictionary.core.DictionaryService;

import java.io.File;
import java.io.IOException;

/**
 * Http Server implementation
 * @author <a href="http://hasan.we4tech.com">nhm tanveer...(hasan)</a>
 */
public class HttpServer {
  private static final String PROPERTY_INDEX_DIR = "config.indexDir";

  private final Logger mLogger = LogManager.getLogger(getClass());
  private int mMaxThreads = 20;
  private String mHttpHost = "0.0.0.0";
  private int mHttpPort = 1992;
  private SelectChannelConnector mConnector;
  private BoundedThreadPool mThreadPool;

  public void startService() throws Exception {
    mLogger.info("Starting http service.");

    // load configuration
    loadConfiguration();

    // create new server
    final Server server = new Server();

    // setup server connector
    server.addConnector(makeConnector());

    // setup thread pool
    server.setThreadPool(makeThreadPool());

    // setting up web context
    server.addHandler(makeWebContext());

    // start server
    server.start();
  }

  private void loadConfiguration() {
    if (System.getProperty(PROPERTY_INDEX_DIR) == null) {
      mLogger.info("No -Dconfig.indexDir is mentioned. now setting " +
                   "up default property.");
      System.setProperty(PROPERTY_INDEX_DIR,
          "/Users/nhmtanveerhossainkhanhasan/java-tmp/dict-index");
    } else {
      mLogger.info("Using default index directory.");
    }
    DOMConfigurator.configure(new File("config/log4j.xml").getAbsolutePath());
  }

  private Handler makeWebContext() throws IOException {
    final Context context = new Context(Context.SESSIONS);
    context.setContextPath("/");
    context.addServlet(JspServlet.class, "*.jsp");
    context.addServlet(DictionaryServiceServlet.class, "/spellchecker/service/*");
    return context;
  }

  private ThreadPool makeThreadPool() {
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("Setting up thread pool of " + mMaxThreads);
    }
    mThreadPool = new BoundedThreadPool();
    mThreadPool.setMaxThreads(mMaxThreads);
    return mThreadPool;
  }

  private Connector makeConnector() {
    if (mLogger.isDebugEnabled()) {
      mLogger.debug("Setting up connctor for - " + mHttpHost + ":" + mHttpPort);
    }
    mConnector =
        new SelectChannelConnector();
    mConnector.setHost(mHttpHost);
    mConnector.setPort(mHttpPort);
    return mConnector;
  }

  public static void main(String[] pArgs) throws Exception {
    final HttpServer httpServer = new HttpServer();
    httpServer.startService();
  }
}
