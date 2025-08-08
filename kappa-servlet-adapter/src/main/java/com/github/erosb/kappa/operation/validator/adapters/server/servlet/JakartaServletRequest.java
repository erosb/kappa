package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.github.erosb.kappa.operation.validator.model.Request;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import static java.util.Objects.requireNonNull;

public abstract class JakartaServletRequest
  implements Request {
  private static final String HTTP_GET = "GET";
  private static final String ERR_MSG = "A HttpServletRequest is required";

  private JakartaServletRequest() {
  }

  /**
   * Creates a wrapped request from a servlet request.
   *
   * @param hsr The given server request.
   * @return The wrapped request to work this.
   */
  public static Request of(HttpServletRequest hsr) throws IOException {
    return of(hsr, hsr.getInputStream());
  }

  /**
   * Creates a wrapped request from a servlet request.
   *
   * @param hsr  The given server request.
   * @param body The body to consume.
   * @return The wrapped request to work this.
   */
  public static Request of(HttpServletRequest hsr, InputStream body) {
    requireNonNull(hsr, ERR_MSG);

    // Method & path
    final DefaultRequest.Builder builder = new DefaultRequest.Builder(
      hsr.getRequestURL().toString(),
      Request.Method.getMethod(hsr.getMethod()));

    // Query string or body
    if (HTTP_GET.equalsIgnoreCase(hsr.getMethod())) {
      builder.query(hsr.getQueryString());
    } else {
      builder.body(Body.from(body));
    }

    // Cookies
    if (hsr.getCookies() != null) {
      for (Cookie cookie : hsr.getCookies()) {
        builder.cookie(cookie.getName(), cookie.getValue());
      }
    }

    // Headers
    Enumeration<String> headerNames = hsr.getHeaderNames();
    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        Enumeration<String> headerValues = hsr.getHeaders(headerName);

        while (headerValues.hasMoreElements()) {
          builder.header(headerName, headerValues.nextElement());
        }
      }
    }

    return builder.build();
  }
}
