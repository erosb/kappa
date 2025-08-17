package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.erosb.kappa.operation.validator.model.Request;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class JakartaServletTest {
  private static final String URL = "http://localhost:8080/path";
  private static final String PATH = "/path";
  private static final String H_NAME = "headerName";
  private static final String H_VALUE = "headerValue";

  private HttpServletRequest servletRequest;
  private Cookie cookie;

  @Before
  public void setUp()
    throws IOException {
    servletRequest = Mockito.mock(HttpServletRequest.class);
    cookie = null;

    Mockito.when(servletRequest.getRequestURL()).thenReturn(new StringBuffer(URL));
    Mockito.when(servletRequest.getQueryString()).thenReturn("id=2&name=foo");
  }

  private void mockCookies(boolean enable) {
    if (enable) {
      cookie = new Cookie("bis", "cuit");
      Cookie[] cookies = new Cookie[]{cookie};
      Mockito.when(servletRequest.getCookies()).thenReturn(cookies);
    } else {
      Mockito.when(servletRequest.getCookies()).thenReturn(null);
    }
  }

  private void mockHeaders(boolean enable) {
    if (enable) {
      Vector<String> headerNames = new Vector<>();
      headerNames.add(H_NAME);
      headerNames.add("Content-Type");

      Mockito.when(servletRequest.getHeaderNames()).thenReturn(headerNames.elements());
      Vector<String> headerValues = new Vector<>();
      headerValues.add(H_VALUE);
      Mockito.when(servletRequest.getHeaders(H_NAME)).thenReturn(headerValues.elements());
      headerValues = new Vector<>();
      headerValues.add("atype");
      Mockito.when(servletRequest.getHeaders("Content-Type")).thenReturn(headerValues.elements());
    } else {
      Mockito.when(servletRequest.getHeaders(H_NAME)).thenReturn(null);
      Mockito.when(servletRequest.getHeaders("Content-Type")).thenReturn(null);
    }
  }

  @Test
  public void basicTest()
    throws IOException {
    servletRequest = MockHttpServletRequestBuilder.get()
      .withDefaultCookies(false)
      .withDefaultHeaders(false)
      .build();

    Request rq = JakartaServletRequest.of(servletRequest);
    checkCommons(rq, false, false);

    Assert.assertEquals(servletRequest.getQueryString(), rq.getQuery());
  }

  @Test
  public void getTest()
    throws IOException, URISyntaxException {
    mockCookies(true);
    mockHeaders(true);
    servletRequest = MockHttpServletRequestBuilder.get().build();

    Request rq = JakartaServletRequest.of(servletRequest);
    checkCommons(rq, true, true);

    Assert.assertEquals("?queryString", rq.getQuery());
    Assert.assertEquals("{\n  \n}", rq.getBody().contentAsNode("application/json", new URI("anything")).toString());
  }

  @Test
  public void postTest()
    throws IOException {
    mockCookies(true);
    mockHeaders(true);
    servletRequest = MockHttpServletRequestBuilder.post()
      .build();

    Request rq = JakartaServletRequest.of(servletRequest);
    checkCommons(rq, true, true);

    Assert.assertEquals(
      JsonNodeFactory.instance.textNode("{}"),
      rq.getBody().getContentAsNode(null, null, null));

    Assert.assertEquals("?queryString", rq.getQuery());
  }

  private void checkCommons(Request rq, boolean checkCookies, boolean checkHeaders) {
    Assert.assertEquals(PATH, rq.getPath());

    if (checkCookies) {
      Assert.assertNotNull(rq.getCookies());
      Assert.assertTrue(rq.getCookies().containsKey(cookie.getName()));
      Assert.assertEquals(cookie.getValue(), rq.getCookies().get(cookie.getName()));
    }

    if (checkHeaders) {
      Assert.assertEquals("application/json", rq.getContentType());
      Assert.assertNotNull(rq.getHeaders());
      Assert.assertTrue(rq.getHeaders().containsKey(H_NAME));
      Assert.assertEquals(H_VALUE, rq.getHeaders().get(H_NAME).iterator().next());
    }
  }
}
