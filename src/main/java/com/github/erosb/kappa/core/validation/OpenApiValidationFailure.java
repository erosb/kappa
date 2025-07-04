package com.github.erosb.kappa.core.validation;

import com.github.erosb.jsonsKema.JsonParseException;
import com.github.erosb.jsonsKema.JsonPointer;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.jsonsKema.TextLocation;
import com.github.erosb.jsonsKema.ValidationFailure;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class OpenApiValidationFailure {

  public static PathValidationFailure noMatchingPathPatternFound(SourceLocation schemaLocation) {
    return new PathValidationFailure("Path template '%s' has not been found from value '%s'.", schemaLocation);
  }

  public static RequestBodyValidationFailure missingRequiredBody() {
    return new RequestBodyValidationFailure("Body is required but none provided.");
  }

  public static RequestBodyValidationFailure unparseableRequestBody(JsonParseException ex) {
    return new RequestBodyValidationFailure("could not parse request body: " + ex.getMessage(), ex.getLocation());
  }

  public static RequestBodyValidationFailure missingContentTypeHeader() {
    return new RequestBodyValidationFailure("Body content type cannot be determined. No 'Content-Type' header available.");
  }

  public static RequestBodyValidationFailure wrongContentType(String actualContentType) {
    return new RequestBodyValidationFailure(
      String.format("Content type '%s' is not allowed for body content.", actualContentType));
  }

  public static ParameterValidationFailure missingRequiredParameter(String paramName, SourceLocation schemaLocation) {
    return new ParameterValidationFailure(String.format("Missing required parameter '%s'.", paramName), schemaLocation);
  }

  public static SchemaValidationFailure bodySchemaValidationFailure(ValidationFailure result) {
    return new SchemaValidationFailure(result);
  }

  public static StatusCodeValidationFailure unknownStatusCode(int unknownStatusCode, SourceLocation schemaLocation) {
    return new StatusCodeValidationFailure(unknownStatusCode, schemaLocation);
  }

  public static class StatusCodeValidationFailure
    extends OpenApiValidationFailure {

    StatusCodeValidationFailure(int unknownStatusCode, SourceLocation schemaLocation) {
      super("Unknown status code " + unknownStatusCode, new SourceLocation(-1, -1,
        new JsonPointer(),
        new URIFactory().responseStatusCode()), schemaLocation);
    }
  }

  public static class SchemaValidationFailure
    extends OpenApiValidationFailure {

    private final ValidationFailure failure;

    private SchemaValidationFailure(ValidationFailure result) {
      super(result.getMessage(), result.getInstance().getLocation(), result.getSchema().getLocation());
      this.failure = result;
    }

    @Override
    public String describeSchemaLocation() {
      return super.describeSchemaLocation();// + " \n\t(evaluated on dynamic path: " + failure.getDynamicPath() + ")";
    }

    public ValidationFailure getFailure() {
      return failure;
    }
  }

  public static class PathValidationFailure
    extends OpenApiValidationFailure {

    PathValidationFailure(String message, SourceLocation schemaLocation) {
      super(message,
        new SourceLocation(-1, -1, new JsonPointer("path"), request),
        schemaLocation);
    }

  }

  public static class ParameterValidationFailure
    extends OpenApiValidationFailure {

    ParameterValidationFailure(String message, SourceLocation schemaLocation) {
      super(message, new SourceLocation(-1, -1, new JsonPointer("parameters"), request),
        schemaLocation);
    }
  }

  public static class RequestBodyValidationFailure
    extends OpenApiValidationFailure {

    public RequestBodyValidationFailure(String message) {
      this(message, new TextLocation(-1, -1, requestBody));
    }

    public RequestBodyValidationFailure(String message, TextLocation parseFailure) {
      super(message, new SourceLocation(parseFailure.getLineNumber(), parseFailure.getPosition(), new JsonPointer(), requestBody),
        new SourceLocation(-1, -1,
          new JsonPointer(), new URIFactory().requestBodyDefinition()));
    }
  }

  private static final URI request = new URIFactory().request();

  private static final URI requestBody = new URIFactory().httpEntity();

  private final String message;

  private final SourceLocation instanceLocation;

  private final SourceLocation schemaLocation;

  OpenApiValidationFailure(String message, SourceLocation instanceLocation,
                           SourceLocation schemaLocation) {
    this.message = requireNonNull(message);
    this.instanceLocation = requireNonNull(instanceLocation);
    this.schemaLocation = requireNonNull(schemaLocation);
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
    return loc.getDocumentSource() + pointerDescr;
  }

  public String describeInstanceLocation() {
    return stringify(instanceLocation) + ((instanceLocation.getLineNumber() > -1) ? (" (line "
      + instanceLocation.getLineNumber() + ", position "
      + instanceLocation.getPosition() + ")") : "");
  }

  public String describeSchemaLocation() {
    return stringify(schemaLocation);
  }

  public SourceLocation getInstanceLocation() {
    return instanceLocation;
  }

  public SourceLocation getSchemaLocation() {
    return schemaLocation;
  }

  @Override
  public String toString() {
    return "OpenApiValidationFailure{" +
      "message='" + message + '\'' +
      '}';
  }
}
