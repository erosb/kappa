package com.github.erosb.kappa.core.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class RequestScopedURIFactory
  extends URIFactory {
  @Override
  public URI httpEntity() {
    return uri("$request.body");
  }
}

class ResponseScopedURIFactory
  extends URIFactory {
  @Override
  public URI httpEntity() {
    return uri("$response.body");
  }
}

public class URIFactory {

  public static URIFactory forRequest() {
    return new RequestScopedURIFactory();
  }

  public static URIFactory forResponse() {
    return new ResponseScopedURIFactory();
  }

  static URI uri(String s) {
    try {
      return new URI(s);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public URI httpEntity() {
    return uri("$request.body");
  }

  public URI requestBodyDefinition() {
    return uri("requestBody");
  }

  public URI request() {
    return uri("$request");
  }

  public URI pathParam(String paramName) {
    return uri("$request.path." + paramName);
  }

  public URI pathParamDefinition(URL contextbaseURL, String paramName) {
    try {
      return new URI(contextbaseURL.toURI() + "/paths/" + paramName);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public URI responseStatusCode() {
    return uri("$response.status");
  }
}
