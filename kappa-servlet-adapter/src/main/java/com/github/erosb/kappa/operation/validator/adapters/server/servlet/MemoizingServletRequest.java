package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Enumeration;
import java.util.Map;

class CachedBodyServletInputStream
  extends ServletInputStream {

  private InputStream cachedBodyInputStream;

  public CachedBodyServletInputStream(byte[] cachedBody) {
    this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
  }

  @Override
  public boolean isFinished() {
    try {
      return cachedBodyInputStream.available() == 0;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setReadListener(ReadListener readListener) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int read()
    throws IOException {
    return cachedBodyInputStream.read();
  }
}

/**
 * This class (and also CachedBodyServletInputStream) were written based on these articles:
 *
 * <ul>
 * <li><a href="https://www.baeldung.com/convert-input-stream-to-array-of-bytes">Convert InputStream to Byte Array</a>
 * <li><a href="https://www.baeldung.com/spring-reading-httpservletrequest-multiple-times">Reading HttpServletRequest Multiple Times in Spring</a>
 * </ul>
 */
public class MemoizingServletRequest
  extends HttpServletRequestWrapper {

  private final byte[] cachedBody;

  public MemoizingServletRequest(HttpServletRequest request)
    throws IOException {
    super(request);
    System.out.println(request.getContentType());
    InputStream requestInputStream = request.getInputStream();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[1000];

    while ((nRead = requestInputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    buffer.flush();
    this.cachedBody = buffer.toByteArray();
    System.out.println("memoized body: " + new String(cachedBody));
  }

  @Override
  public ServletInputStream getInputStream()
    throws IOException {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  @Override
  public BufferedReader getReader()
    throws IOException {
    // Create a reader from cachedContent
    // and return it
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
    return new BufferedReader(new InputStreamReader(byteArrayInputStream));
  }
}
