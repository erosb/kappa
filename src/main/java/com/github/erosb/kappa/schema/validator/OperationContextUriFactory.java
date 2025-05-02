package com.github.erosb.kappa.schema.validator;

import com.github.erosb.jsonsKema.JsonPointer;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.validation.URIFactory;

import java.net.URISyntaxException;

public class OperationContextUriFactory
  extends URIFactory {

  private final OAIContext context;

  private final String templatePath;

  private final String method;

  public OperationContextUriFactory(OAIContext context, String templatePath, String method) {
    this.context = context;
    this.templatePath = templatePath;
    this.method = method;
  }

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
