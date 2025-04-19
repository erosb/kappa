package com.github.erosb.kappa.core.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class RequestScopedURIFactory
  extends URIFactory {

  RequestScopedURIFactory(URL contextBaseURL) {
    super(contextBaseURL);
  }

  @Override
  public URI httpEntity() {
    return uri("$request.body");
  }
}

class ResponseScopedURIFactory
  extends URIFactory {

  ResponseScopedURIFactory(URL contextBaseURL) {
    super(contextBaseURL);
  }

  @Override
  public URI httpEntity() {
    return uri("$response.body");
  }
}

public class URIFactory {

  static URI uri(String s) {
    try {
      return new URI(s);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private final URL contextBaseURL;

  private String pathName;

  public URIFactory(URL contextBaseURL) {
    this.contextBaseURL = contextBaseURL;
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

  public URI pathParamDefinition(String paramName) {
    try {
      return new URI(contextBaseURL.toURI() + "/paths/" + paramName);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public URI instanceResponseStatusCode() {
    return uri("$response.status");
  }

  public URI definitionResponseStatusCode(String pathName, String methodName) {
    System.out.println("contextBaseURL = " + contextBaseURL);
    try {
      return uri(contextBaseURL.toURI() + "/" + methodName + "/responses");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public URIFactory forRequest() {
    return new RequestScopedURIFactory(contextBaseURL);
  }

  public URIFactory forResponse() {
    return new ResponseScopedURIFactory(contextBaseURL);
  }
}
