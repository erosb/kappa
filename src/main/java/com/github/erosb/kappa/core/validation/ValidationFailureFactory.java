package com.github.erosb.kappa.core.validation;

import com.github.erosb.jsonsKema.JsonParseException;
import com.github.erosb.jsonsKema.TextLocation;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class ValidationFailureFactory {
  private final URIFactory uriFactory;

  public ValidationFailureFactory(URIFactory uriFactory) {
    this.uriFactory = requireNonNull(uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure missingRequiredBody() {
    return new OpenApiValidationFailure.RequestBodyValidationFailure("Body is required but none provided.",
      new TextLocation(-1, -1, uriFactory.request()),
      uriFactory.request(),
      uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure unparseableRequestBody(JsonParseException ex, URI uri) {
    return new OpenApiValidationFailure.RequestBodyValidationFailure("could not parse request body: " + ex.getMessage(),
      ex.getLocation(), uri, uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure missingContentTypeHeader() {
    return new OpenApiValidationFailure.RequestBodyValidationFailure(
      "Body content type cannot be determined. No 'Content-Type' header available.",
      new TextLocation(-1, -1, uriFactory.request()),
      uriFactory.request(),
      uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure wrongContentType(String actualContentType) {
    return new OpenApiValidationFailure.RequestBodyValidationFailure(
      String.format("Content type '%s' is not allowed for body content.", actualContentType),
      new TextLocation(-1, -1, uriFactory.request()),
      uriFactory.request(),
      uriFactory
    );
  }

  public OpenApiValidationFailure.StatusCodeValidationFailure unknownStatusCode(int unknownStatusCode, URI operationUri) {
    return new OpenApiValidationFailure.StatusCodeValidationFailure(unknownStatusCode, operationUri, uriFactory);
  }

  public OpenApiValidationFailure.PathValidationFailure noMatchingPathPatternFound() {
    return new OpenApiValidationFailure.PathValidationFailure("Path template '%s' has not been found from value '%s'.",
      uriFactory);
  }

  public OpenApiValidationFailure.ParameterValidationFailure missingRequiredParameter(String paramName) {
    return new OpenApiValidationFailure.ParameterValidationFailure(String.format("Missing required parameter '%s'.", paramName),
      uriFactory);
  }

}

