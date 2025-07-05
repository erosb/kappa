package com.github.erosb.kappa.core.validation;

import com.github.erosb.jsonsKema.JsonPointer;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.kappa.core.model.OAIContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static java.util.Objects.requireNonNull;

public abstract class URIFactory {

  static URI uri(String s) {
    try {
      return new URI(s);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public abstract URI httpEntity();

  public static URI request() {
    return uri("$request");
  }

  public URI pathParam(String paramName) {
    return uri("$request.path." + paramName);
  }

  public static URI responseStatusCode() {
    return uri("$response.status");
  }

  public static URI requestBody() {
    return uri("$request.body");
  }
}
