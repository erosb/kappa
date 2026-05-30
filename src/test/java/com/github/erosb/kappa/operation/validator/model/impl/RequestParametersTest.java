package com.github.erosb.kappa.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonString;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RequestParametersTest {

  public static final JsonString JSON_STR_VALUE = new JsonString("value");

  @Test
  public void nullCheck() {
    RequestParameters params = new RequestParameters(null, null, null, null);

    assertNull(params.getCookieParameters());
    assertNull(params.getHeaderParameters());
    assertNull(params.getPathParameters());
    assertNull(params.getQueryParameters());

    assertNull(params.getCookieParameter("foo"));
    assertNull(params.getHeaderParameter("foo"));
    assertNull(params.getPathParameter("foo"));
    assertNull(params.getQueryParameter("foo"));
  }

  @Test
  public void pathCheck() {
    Map<String, IJsonValue> values = new HashMap<>();
    values.put("key", JSON_STR_VALUE);
    RequestParameters params = new RequestParameters(values, null, null, null);

    assertNotNull(params.getPathParameters());

    assertEquals(JSON_STR_VALUE, params.getPathParameter("key"));
  }

  @Test
  public void queryCheck() {
    Map<String, IJsonValue> values = new HashMap<>();
    values.put("key", JSON_STR_VALUE);
    RequestParameters params = new RequestParameters(null, values, null, null);

    assertNotNull(params.getQueryParameters());

    assertEquals(JSON_STR_VALUE, params.getQueryParameter("key"));
  }

  @Test
  public void headerCheck() {
    Map<String, IJsonValue> values = new HashMap<>();
    values.put("key", JSON_STR_VALUE);
    RequestParameters params = new RequestParameters(null, null, values, null);

    assertNotNull(params.getHeaderParameters());

    assertEquals(JSON_STR_VALUE, params.getHeaderParameter("key"));
  }

  @Test
  public void cookieCheck() {
    Map<String, IJsonValue> values = new HashMap<>();
    values.put("key", JSON_STR_VALUE);
    RequestParameters params = new RequestParameters(null, null, null, values);

    assertNotNull(params.getCookieParameters());

    assertEquals(JSON_STR_VALUE, params.getCookieParameter("key"));
  }
}
