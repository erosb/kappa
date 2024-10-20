package org.openapi4j.core.validation;

import com.github.erosb.jsonsKema.ValidationFailure;

public class OpenApiValidationFailure {

  public static PathValidationFailure noMatchingPathPatternFound() {
    return new PathValidationFailure("Path template '%s' has not been found from value '%s'.");
  }

  public static RequestBodyValidationFailure missingRequiredBody() {
    return new RequestBodyValidationFailure("Body is required but none provided.");
  }

  public static RequestBodyValidationFailure missingContentTypeHeader() {
    return new RequestBodyValidationFailure("Body content type cannot be determined. No 'Content-Type' header available.");
  }

  public static RequestBodyValidationFailure wrongContentType(String actualContentType) {
    return new RequestBodyValidationFailure("Content type '%s' is not allowed for body content.".formatted(actualContentType));
  }

  public static ParameterValidationFailure missingRequiredParameter(String paramName) {
    return new ParameterValidationFailure("Content type '%s' is not allowed for body content.".formatted(paramName));
  }

  public static SchemaValidationFailure bodySchemaValidationFailure(ValidationFailure result) {
    return new SchemaValidationFailure(result);
  }

  public static class SchemaValidationFailure extends OpenApiValidationFailure {

    private final ValidationFailure failure;

    private SchemaValidationFailure(ValidationFailure result) {
      super(result.getMessage());
      this.failure = result;
    }

    public ValidationFailure getFailure() {
      return failure;
    }
  }

  public static class PathValidationFailure extends OpenApiValidationFailure {

    PathValidationFailure(String message) {
      super(message);
    }

  }

  public static class ParameterValidationFailure extends OpenApiValidationFailure {

    ParameterValidationFailure(String message) {
      super(message);
    }
  }

  public static class RequestBodyValidationFailure extends OpenApiValidationFailure {

    private RequestBodyValidationFailure(String message) {
      super(message);
    }
  }

  private final String message;

  OpenApiValidationFailure(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
