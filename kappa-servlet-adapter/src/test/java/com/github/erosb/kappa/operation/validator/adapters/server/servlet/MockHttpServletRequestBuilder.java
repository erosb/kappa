package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockHttpServletRequestBuilder {

  private static class MockServletInputStream
    extends ServletInputStream {
    private final InputStream sourceStream;

    MockServletInputStream(InputStream sourceStream) {
      this.sourceStream = sourceStream;
    }

    @Override
    public int read()
      throws IOException {
      return sourceStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len)
      throws IOException {
      return sourceStream.read(b, off, len);
    }

    @Override
    public int read(byte[] b)
      throws IOException {
      return sourceStream.read(b);
    }

    public void close()
      throws IOException {
      sourceStream.close();
    }

    @Override
    public boolean isFinished() {
      return false;
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
  }

  private static final String URL = "http://localhost:8080/path";
  private static final String H_NAME = "headerName";
  private static final String H_VALUE = "headerValue";
  private boolean withDefaultCookies = true;
  private boolean withDefaultHeaders = true;
  private String method = "GET";

  public static MockHttpServletRequestBuilder get() {
    return new MockHttpServletRequestBuilder().method("GET");
  }

  public static MockHttpServletRequestBuilder post() {
    return new MockHttpServletRequestBuilder().method("POST");
  }

  private MockHttpServletRequestBuilder method(String method) {
    this.method = method;
    return this;
  }

  public MockHttpServletRequestBuilder withDefaultCookies(boolean withDefaultCookies) {
    this.withDefaultCookies = withDefaultCookies;
    return this;
  }

  public MockHttpServletRequestBuilder withDefaultHeaders(boolean withDefaultHeaders) {
    this.withDefaultHeaders = withDefaultHeaders;
    return this;
  }

  public HttpServletRequest build() {
    HttpServletRequest req = mock(HttpServletRequest.class);
    when(req.getRequestURL()).thenReturn(new StringBuffer(URL));
    when(req.getMethod()).thenReturn(method);
    when(req.getCookies()).thenReturn(withDefaultCookies ? new Cookie[]{new Cookie("bis", "cuit")} : null);
    when(req.getQueryString()).thenReturn("?queryString");
    if (withDefaultHeaders) {
      Vector<String> headerNames = new Vector<>();
      headerNames.add(H_NAME);
      headerNames.add("Content-Type");

      when(req.getHeaderNames()).thenReturn(headerNames.elements());
      Vector<String> headerValues = new Vector<>();
      headerValues.add(H_VALUE);
      when(req.getHeaders(H_NAME)).thenReturn(headerValues.elements());
      headerValues = new Vector<>();
      headerValues.add("application/json");
      when(req.getHeaders("Content-Type")).thenReturn(headerValues.elements());
    }
    MockServletInputStream msis = new MockServletInputStream(new ByteArrayInputStream("{}".getBytes()));
    try {
      when(req.getInputStream()).thenReturn(msis);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return req;
  }
}

