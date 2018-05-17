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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class Main
{
  private static class MyProxy extends AsyncProxyServlet
  {
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
            MyProxy.super.service(new QueryForwardingRequestWrapper(r), res);
          }
          catch (Exception e) {

          }
        }
      };
      t.start();
      r.startAsync();
      return;
    }

    protected String rewriteTarget(HttpServletRequest request)
    {
      return "http://localhost/";
    }
  }

  private static void reverseProxy() throws Exception
  {
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


  public static void main(String[] args) throws Exception
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

    public String getAuthType()
    {
      return realRequest.getAuthType();
    }

    public Cookie[] getCookies()
    {
      return realRequest.getCookies();
    }

    public long getDateHeader(String name)
    {
      return realRequest.getDateHeader(name);
    }

    public String getHeader(String name)
    {
      return realRequest.getHeader(name);
    }

    public Enumeration<String> getHeaders(String name)
    {
      return realRequest.getHeaders(name);
    }

    public Enumeration<String> getHeaderNames()
    {
      return realRequest.getHeaderNames();
    }

    public int getIntHeader(String name)
    {
      return realRequest.getIntHeader(name);
    }

    public String getMethod()
    {
      return realRequest.getMethod();
    }

    public String getPathInfo()
    {
      return realRequest.getPathInfo();
    }

    public String getPathTranslated()
    {
      return realRequest.getPathTranslated();
    }

    public String getContextPath()
    {
      return realRequest.getContextPath();
    }

    public String getQueryString()
    {
      return realRequest.getQueryString();
    }

    public String getRemoteUser()
    {
      return realRequest.getRemoteUser();
    }

    public boolean isUserInRole(String role)
    {
      return realRequest.isUserInRole(role);
    }

    public Principal getUserPrincipal()
    {
      return realRequest.getUserPrincipal();
    }

    public String getRequestedSessionId()
    {
      return realRequest.getRequestedSessionId();
    }

    public String getRequestURI()
    {
      return realRequest.getRequestURI();
    }

    public StringBuffer getRequestURL()
    {
      return realRequest.getRequestURL();
    }

    public String getServletPath()
    {
      return realRequest.getServletPath();
    }

    public HttpSession getSession(boolean create)
    {
      return realRequest.getSession(create);
    }

    public HttpSession getSession()
    {
      return realRequest.getSession();
    }

    public String changeSessionId()
    {
      return realRequest.changeSessionId();
    }

    public boolean isRequestedSessionIdValid()
    {
      return realRequest.isRequestedSessionIdValid();
    }

    public boolean isRequestedSessionIdFromCookie()
    {
      return realRequest.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL()
    {
      return realRequest.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl()
    {
      return realRequest.isRequestedSessionIdFromUrl();
    }

    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException
    {
      return realRequest.authenticate(response);
    }

    public void login(String username, String password) throws ServletException
    {
      realRequest.login(username, password);
    }

    public void logout() throws ServletException
    {
      realRequest.logout();
    }

    public Collection<Part> getParts() throws IOException, ServletException
    {
      return realRequest.getParts();
    }

    public Part getPart(String name) throws IOException, ServletException
    {
      return realRequest.getPart(name);
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException
    {
      return realRequest.upgrade(handlerClass);
    }

    public Object getAttribute(String name)
    {
      return realRequest.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames()
    {
      return realRequest.getAttributeNames();
    }

    public String getCharacterEncoding()
    {
      return realRequest.getCharacterEncoding();
    }

    public void setCharacterEncoding(String env) throws UnsupportedEncodingException
    {
      realRequest.setCharacterEncoding(env);
    }

    public int getContentLength()
    {
      return realRequest.getContentLength();
    }

    public long getContentLengthLong()
    {
      return realRequest.getContentLengthLong();
    }

    public String getContentType()
    {
      return realRequest.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException
    {
      return realRequest.getInputStream();
    }

    public String getParameter(String name)
    {
      return realRequest.getParameter(name);
    }

    public Enumeration<String> getParameterNames()
    {
      return realRequest.getParameterNames();
    }

    public String[] getParameterValues(String name)
    {
      return realRequest.getParameterValues(name);
    }

    public Map<String, String[]> getParameterMap()
    {
      return realRequest.getParameterMap();
    }

    public String getProtocol()
    {
      return realRequest.getProtocol();
    }

    public String getScheme()
    {
      return realRequest.getScheme();
    }

    public String getServerName()
    {
      return realRequest.getServerName();
    }

    public int getServerPort()
    {
      return realRequest.getServerPort();
    }

    public BufferedReader getReader() throws IOException
    {
      return realRequest.getReader();
    }

    public String getRemoteAddr()
    {
      return realRequest.getRemoteAddr();
    }

    public String getRemoteHost()
    {
      return realRequest.getRemoteHost();
    }

    public void setAttribute(String name, Object o)
    {
      realRequest.setAttribute(name, o);
    }

    public void removeAttribute(String name)
    {
      realRequest.removeAttribute(name);
    }

    public Locale getLocale()
    {
      return realRequest.getLocale();
    }

    public Enumeration<Locale> getLocales()
    {
      return realRequest.getLocales();
    }

    public boolean isSecure()
    {
      return realRequest.isSecure();
    }

    public RequestDispatcher getRequestDispatcher(String path)
    {
      return realRequest.getRequestDispatcher(path);
    }

    public String getRealPath(String path)
    {
      return realRequest.getRealPath(path);
    }

    public int getRemotePort()
    {
      return realRequest.getRemotePort();
    }

    public String getLocalName()
    {
      return realRequest.getLocalName();
    }

    public String getLocalAddr()
    {
      return realRequest.getLocalAddr();
    }

    public int getLocalPort()
    {
      return realRequest.getLocalPort();
    }

    public ServletContext getServletContext()
    {
      return realRequest.getServletContext();
    }

    public AsyncContext startAsync() throws IllegalStateException
    {
      if (realRequest.isAsyncStarted()) {
        return realRequest.getAsyncContext();
      } else {
        return realRequest.startAsync();
      }
    }

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
    public boolean isAsyncStarted()
    {
      return realRequest.isAsyncStarted();
    }

    public boolean isAsyncSupported()
    {
      return realRequest.isAsyncSupported();
    }

    public AsyncContext getAsyncContext()
    {
      return realRequest.getAsyncContext();
    }

    public DispatcherType getDispatcherType()
    {
      return realRequest.getDispatcherType();
    }
  }
}
