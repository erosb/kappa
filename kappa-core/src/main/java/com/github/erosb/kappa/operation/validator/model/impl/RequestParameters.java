package com.github.erosb.kappa.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.jsonsKema.IJsonValue;

import java.util.Map;

public class RequestParameters {
  private final Map<String, IJsonValue> pathParameters;
  private final Map<String, IJsonValue> queryParameters;
  private final Map<String, IJsonValue> headerParameters;
  private final Map<String, IJsonValue> cookieParameters;

  public RequestParameters(Map<String, IJsonValue> pathParameters,
                           Map<String, IJsonValue> queryParameters,
                           Map<String, IJsonValue> headerParameters,
                           Map<String, IJsonValue> cookieParameters) {

    this.pathParameters = pathParameters;
    this.queryParameters = queryParameters;
    this.headerParameters = headerParameters;
    this.cookieParameters = cookieParameters;
  }

  public Map<String, IJsonValue> getPathParameters() {
    return pathParameters;
  }

  public IJsonValue getPathParameter(String name) {
    if (pathParameters == null) {
      return null;
    }

    return pathParameters.get(name);
  }

  public Map<String, IJsonValue> getQueryParameters() {
    return queryParameters;
  }

  public IJsonValue getQueryParameter(String name) {
    if (queryParameters == null) {
      return null;
    }

    return queryParameters.get(name);
  }

  public Map<String, IJsonValue> getHeaderParameters() {
    return headerParameters;
  }

  public IJsonValue getHeaderParameter(String name) {
    if (headerParameters == null) {
      return null;
    }

    return headerParameters.get(name);
  }

  public Map<String, IJsonValue> getCookieParameters() {
    return cookieParameters;
  }

  public IJsonValue getCookieParameter(String name) {
    if (cookieParameters == null) {
      return null;
    }

    return cookieParameters.get(name);
  }
}
