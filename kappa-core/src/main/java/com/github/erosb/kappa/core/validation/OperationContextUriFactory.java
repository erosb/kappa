package com.github.erosb.kappa.core.validation;

import com.github.erosb.jsonsKema.JsonPointer;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.validation.URIFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import static java.util.Objects.requireNonNull;

class RequestScopedURIFactory
  extends OperationContextUriFactory {

  public RequestScopedURIFactory(OAIContext context, String templatePath, String method) {
    super(context, templatePath, method);
  }

  @Override
  public SourceLocation definitionHttpEntity() {
    try {
      return new SourceLocation(
        -1, -1,
        new JsonPointer("paths", templatePath, method, "requestBody"),
        context.getBaseUrl().toURI()
      );
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public URI httpEntity() {
    return URIFactory.requestBody();
  }
}

class ResponseScopedURIFactory
  extends OperationContextUriFactory {

  private final String responseCodeDefinition;

  public ResponseScopedURIFactory(OAIContext context, String templatePath, String method, String responseCodeDefinition) {
    super(context, templatePath, method);
    this.responseCodeDefinition = requireNonNull(responseCodeDefinition);
  }

  @Override
  public SourceLocation definitionHttpEntity() {
    try {
      return new SourceLocation(
        -1, -1,
        new JsonPointer("paths", templatePath, method, "responses", responseCodeDefinition, "content"),
        context.getBaseUrl().toURI()
      );
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public URI httpEntity() {
    return uri("$response.body");
  }

}

public abstract class OperationContextUriFactory
  extends URIFactory {

  protected final OAIContext context;

  protected final String templatePath;

  protected final String method;

  public OperationContextUriFactory(OAIContext context, String templatePath, String method) {
    this.context = context;
    this.templatePath = templatePath;
    this.method = method;
  }

  public static OperationContextUriFactory forRequest(OAIContext context, String templatePath, String method) {
    return new RequestScopedURIFactory(context, templatePath, method);
  }

  public static OperationContextUriFactory forResponse(OAIContext context, String templatePath, String method,
                                                       String statusCode) {
    return new ResponseScopedURIFactory(context, templatePath, method, statusCode);
  }

  public abstract SourceLocation definitionHttpEntity();

  public SourceLocation definitionStatusCode() {
    try {
      return new SourceLocation(
        -1, -1,
        new JsonPointer("paths", templatePath, method, "responses"),
        context.getBaseUrl().toURI()
      );
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public SourceLocation definitionPaths() {
    try {
      return new SourceLocation(-1, -1, new JsonPointer("paths"), context.getBaseUrl().toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public URI pathParamDefinition(String paramName) {
    try {
      //      JsonPointer pointer = new JsonPointer("paths", URLEncoder.encode(templatePath), "parameters", paramName);
      //      return new URI(context.getBaseUrl() + pointer.toString());
      //      new SourceLocation(-1, -1, pointer, context.getBaseUrl().toURI());
      return new URI(context.getBaseUrl() + "/paths/" + paramName);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public SourceLocation definitionPath() {
    try {
      return new SourceLocation(
        -1, -1,
        new JsonPointer("paths", templatePath),
        context.getBaseUrl().toURI()
      );
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
