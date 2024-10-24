package com.github.erosb.kappa.core.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

  public URI pathParamDefinition(URL contextbaseURL, String paramName) {
      try {
          return new URI(contextbaseURL.toURI() + "/paths/" + paramName);
      } catch (URISyntaxException e) {
          throw new RuntimeException(e);
      }
  }
}
