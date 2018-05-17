//
//  ========================================================================
//  Copyright (c) 1995-2015 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.proxy.AsyncProxyServlet;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

public class Main
{
  private static class MyProxy extends AsyncProxyServlet
  {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                            IOException
    {
      final HttpServletRequest r = request;
      final HttpServletResponse res = response;
      Thread t = new Thread()
      {
        public void run()
        {
          try {
            System.out.println("haha");
            Thread.sleep(5000);
            MyProxy.super.service(r, res);
          } catch (Exception e) {

          }
        }
      };
      t.start();
      r.startAsync();
      return;
    }

    @Override
    protected String rewriteTarget(HttpServletRequest request)
    {
      return "http://localhost/";
    }
  }
  private static void reverseProxy() throws Exception{
    Server server = new Server();

    ServerConnector connector = new ServerConnector(server);
    connector.setHost("127.0.0.1");
    connector.setPort(8888);

    server.setConnectors(new Connector[]{connector});

    // Setup proxy handler to handle CONNECT methods
    ConnectHandler proxy = new ConnectHandler();
    server.setHandler(proxy);

    // Setup proxy servlet
    ServletContextHandler context = new ServletContextHandler(proxy, "/", ServletContextHandler.SESSIONS);
    ServletHolder proxyServlet = new ServletHolder(AsyncProxyServlet.Transparent.class);
    context.addServlet(proxyServlet, "/*");

    server.start();
  }

  private static void myReverseProxy() throws Exception
  {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setHost("127.0.0.1");
    connector.setPort(8889);

    server.setConnectors(new Connector[]{connector});

    final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
    ServletHolder sh = new ServletHolder(new MyProxy());
    sh.setInitParameter("proxyTo", "http://localhost/");
    sh.setInitParameter("Prefix", "/");
    root.addServlet(sh, "/*");
    HandlerList handlerList = new HandlerList();
    handlerList.setHandlers(
        new Handler[]{
            root
        }
    );
    server.setHandler(handlerList);

    server.start();
  }


  public static void main( String[] args ) throws Exception
  {
    // reverseProxy();
    myReverseProxy();
  }
    private static class QueryForwardingRequestWrapper implements HttpServletRequest
  {
    private final HttpServletRequest realRequest;
    public QueryForwardingRequestWrapper(HttpServletRequest realRequest)
    {
      this.realRequest = realRequest;
    }
    @Override
    public String getAuthType()
    {
      return realRequest.getAuthType();
    }

    @Override
    public Cookie[] getCookies()
    {
      return realRequest.getCookies();
    }

    @Override
    public long getDateHeader(String name)
    {
      return realRequest.getDateHeader(name);
    }

    @Override
    public String getHeader(String name)
    {
      return realRequest.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name)
    {
      return realRequest.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames()
    {
      return realRequest.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name)
    {
      return realRequest.getIntHeader(name);
    }

    @Override
    public String getMethod()
    {
      return realRequest.getMethod();
    }

    @Override
    public String getPathInfo()
    {
      return realRequest.getPathInfo();
    }

    @Override
    public String getPathTranslated()
    {
      return realRequest.getPathTranslated();
    }

    @Override
    public String getContextPath()
    {
      return realRequest.getContextPath();
    }

    @Override
    public String getQueryString()
    {
      return realRequest.getQueryString();
    }

    @Override
    public String getRemoteUser()
    {
      return realRequest.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String role)
    {
      return realRequest.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal()
    {
      return realRequest.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId()
    {
      return realRequest.getRequestedSessionId();
    }

    @Override
    public String getRequestURI()
    {
      return realRequest.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL()
    {
      return realRequest.getRequestURL();
    }

    @Override
    public String getServletPath()
    {
      return realRequest.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create)
    {
      return realRequest.getSession(create);
    }

    @Override
    public HttpSession getSession()
    {
      return realRequest.getSession();
    }

    @Override
    public String changeSessionId()
    {
      return realRequest.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid()
    {
      return realRequest.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
      return realRequest.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL()
    {
      return realRequest.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
      return realRequest.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException
    {
      return realRequest.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException
    {
      realRequest.login(username, password);
    }

    @Override
    public void logout() throws ServletException
    {
      realRequest.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException
    {
      return realRequest.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException
    {
      return realRequest.getPart(name);
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException
    {
      return realRequest.upgrade(handlerClass);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException
    {
      return realRequest.upgrade(handlerClass);
    }

    @Override
    public Object getAttribute(String name)
    {
      return realRequest.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames()
    {
      return realRequest.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding()
    {
      return realRequest.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException
    {
      realRequest.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength()
    {
      return realRequest.getContentLength();
    }

    @Override
    public long getContentLengthLong()
    {
      return realRequest.getContentLengthLong();
    }

    @Override
    public String getContentType()
    {
      return realRequest.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
      return realRequest.getInputStream();
    }

    @Override
    public String getParameter(String name)
    {
      return realRequest.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
      return realRequest.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name)
    {
      return realRequest.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap()
    {
      return realRequest.getParameterMap();
    }

    @Override
    public String getProtocol()
    {
      return realRequest.getProtocol();
    }

    @Override
    public String getScheme()
    {
      return realRequest.getScheme();
    }

    @Override
    public String getServerName()
    {
      return realRequest.getServerName();
    }

    @Override
    public int getServerPort()
    {
      return realRequest.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
      return realRequest.getReader();
    }

    @Override
    public String getRemoteAddr()
    {
      return realRequest.getRemoteAddr();
    }

    @Override
    public String getRemoteHost()
    {
      return realRequest.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o)
    {
      realRequest.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name)
    {
      realRequest.removeAttribute(name);
    }

    @Override
    public Locale getLocale()
    {
      return realRequest.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales()
    {
      return realRequest.getLocales();
    }

    @Override
    public boolean isSecure()
    {
      return realRequest.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
      return realRequest.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path)
    {
      return realRequest.getRealPath(path);
    }

    @Override
    public int getRemotePort()
    {
      return realRequest.getRemotePort();
    }

    @Override
    public String getLocalName()
    {
      return realRequest.getLocalName();
    }

    @Override
    public String getLocalAddr()
    {
      return realRequest.getLocalAddr();
    }

    @Override
    public int getLocalPort()
    {
      return realRequest.getLocalPort();
    }

    @Override
    public ServletContext getServletContext()
    {
      return realRequest.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException
    {
      if (realRequest.isAsyncStarted()) {
        return realRequest.getAsyncContext();
      } else {
        return realRequest.startAsync();
      }
    }

    @Override
    public AsyncContext startAsync(
        ServletRequest servletRequest, ServletResponse servletResponse
    ) throws IllegalStateException
    {
      if (realRequest.isAsyncStarted()) {
        return realRequest.getAsyncContext();
      } else {
        return realRequest.startAsync(servletRequest, servletResponse);
      }
    }

    // Foll the outside functions here
    @Override
    public boolean isAsyncStarted()
    {
      return realRequest.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported()
    {
      return realRequest.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext()
    {
      return realRequest.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType()
    {
      return realRequest.getDispatcherType();
    }
  }
}
