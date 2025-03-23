package com.github.erosb.kappa.core.validation;

import com.github.erosb.jsonsKema.JsonParseException;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class ValidationFailureFactory {
  private final URIFactory uriFactory;

  public ValidationFailureFactory(URIFactory uriFactory) {
    this.uriFactory = requireNonNull(uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure missingRequiredBody() {
    return new OpenApiValidationFailure.RequestBodyValidationFailure("Body is required but none provided.");
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure unparseableRequestBody(JsonParseException ex, URI uri) {
    return new OpenApiValidationFailure.RequestBodyValidationFailure("could not parse request body: " + ex.getMessage(),
      ex.getLocation(), uri, uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure missingContentTypeHeader() {
    return new OpenApiValidationFailure.RequestBodyValidationFailure(
      "Body content type cannot be determined. No 'Content-Type' header available.", uriFactory);
  }

  public OpenApiValidationFailure.RequestBodyValidationFailure wrongContentType(String actualContentType) {
    return new OpenApiValidationFailure.RequestBodyValidationFailure(
      String.format("Content type '%s' is not allowed for body content.", actualContentType));
  }

  public OpenApiValidationFailure.StatusCodeValidationFailure unknownStatusCode(int unknownStatusCode, URI operationUri) {
    return new OpenApiValidationFailure.StatusCodeValidationFailure(unknownStatusCode, operationUri, uriFactory);
  }

  public static OpenApiValidationFailure.PathValidationFailure noMatchingPathPatternFound() {
    return new OpenApiValidationFailure.PathValidationFailure("Path template '%s' has not been found from value '%s'.");
  }

  public static OpenApiValidationFailure.ParameterValidationFailure missingRequiredParameter(String paramName) {
    return new OpenApiValidationFailure.ParameterValidationFailure(String.format("Missing required parameter '%s'.", paramName));
  }

}

