package com.github.erosb.kappa.core.validation;

import com.github.erosb.jsonsKema.JsonPointer;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.jsonsKema.ValidationFailure;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class OpenApiValidationFailure {

  public static PathValidationFailure noMatchingPathPatternFound() {
    return new PathValidationFailure("Path template '%s' has not been found from value '%s'.");
  }

  public static RequestBodyValidationFailure missingRequiredBody() {
    return new RequestBodyValidationFailure("Body is required but none provided.");
  }

  public static RequestBodyValidationFailure unparseableRequestBody(String descr) {
    return new RequestBodyValidationFailure("could not parse request body: " + descr);
  }

  public static RequestBodyValidationFailure missingContentTypeHeader() {
    return new RequestBodyValidationFailure("Body content type cannot be determined. No 'Content-Type' header available.");
  }

  public static RequestBodyValidationFailure wrongContentType(String actualContentType) {
    return new RequestBodyValidationFailure(String.format("Content type '%s' is not allowed for body content.", actualContentType));
  }

  public static ParameterValidationFailure missingRequiredParameter(String paramName) {
    return new ParameterValidationFailure(String.format("Missing required parameter '%s'.", paramName));
  }

  public static SchemaValidationFailure bodySchemaValidationFailure(ValidationFailure result) {
    return new SchemaValidationFailure(result);
  }

  public static class SchemaValidationFailure
    extends OpenApiValidationFailure {

    private final ValidationFailure failure;

    private SchemaValidationFailure(ValidationFailure result) {
      super(result.getMessage(), result.getInstance().getLocation(), result.getSchema().getLocation());
      this.failure = result;
    }

    public ValidationFailure getFailure() {
      return failure;
    }
  }

  public static class PathValidationFailure
    extends OpenApiValidationFailure {

    PathValidationFailure(String message) {
      super(message,
        new SourceLocation(-1, -1, new JsonPointer("path"), request),
        null);
    }

  }

  public static class ParameterValidationFailure
    extends OpenApiValidationFailure {

    ParameterValidationFailure(String message) {
      super(message, new SourceLocation(-1, -1, new JsonPointer("parameters"), request),
        null);
    }
  }

  public static class RequestBodyValidationFailure
    extends OpenApiValidationFailure {

    public RequestBodyValidationFailure(String message) {
      super(message, new SourceLocation(-1, -1, new JsonPointer(), requestBody), new SourceLocation(-1, -1,
        new JsonPointer(), new URIFactory().requestBodyDefinition()));
    }
  }

  private static final URI request = new URIFactory().request();

  private static final URI requestBody = new URIFactory().requestBody();

  private final String message;

  private final SourceLocation instanceLocation;

  private final SourceLocation schemaLocation;

  OpenApiValidationFailure(String message, SourceLocation instanceLocation,
                           SourceLocation schemaLocation) {
    this.message = requireNonNull(message);
    this.instanceLocation = requireNonNull(instanceLocation);
    this.schemaLocation = schemaLocation;
  }

  public String getMessage() {
    return message;
  }

  private String stringify(SourceLocation loc) {
    JsonPointer pointer = loc.getPointer();
    String pointerDescr;
    if (pointer.getSegments().isEmpty()) {
      pointerDescr = "";
    } else {
      pointerDescr = pointer.toString();
    }
    return loc.getDocumentSource().toString() + pointerDescr;
  }

  public String describeInstanceLocation() {
    return stringify(instanceLocation);
  }

  public String describeSchemaLocation() {
    return stringify(schemaLocation);
  }
}
