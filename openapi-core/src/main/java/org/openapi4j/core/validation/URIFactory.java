package org.openapi4j.core.validation;

import java.net.URI;
import java.net.URISyntaxException;

public class URIFactory {

  private static URI uri(String s) {
    try {
      return new URI(s);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public URI requestBody() {
    return uri("$request.body");
  }

  public URI request() {
    return uri("$request");
  }

  public URI pathParam(String paramName) {
    return uri("$request.path." + paramName);
  }
}
