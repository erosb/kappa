package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public interface ValidationFailureSender {

  static ValidationFailureSender defaultSender() {
    return new DefaultValidationFailureSender();
  }

  static ValidationFailureSender rfc9457Sender() {
    return new RFC9457FailureSender();
  }

  void send(ValidationException ex, HttpServletResponse httpResp)
    throws IOException;

}

class DefaultValidationFailureSender
  implements ValidationFailureSender {

  @Override
  public void send(ValidationException ex, HttpServletResponse httpResp)
    throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode respObj = objectMapper.createObjectNode();
    ArrayNode itemsJson = objectMapper.createArrayNode();
    if (ex.results().isEmpty()) {
      ObjectNode itemJson = objectMapper.createObjectNode();
      itemJson.put("message", ex.getMessage());
      itemsJson.add(itemJson);
    }
    ex.results().forEach(item -> {
      ObjectNode itemJson = failureToJson(item, objectMapper);
      itemsJson.add(itemJson);
    });
    respObj.put("errors", itemsJson);
    httpResp.setStatus(400);
    httpResp.getWriter().print(objectMapper
      .writerWithDefaultPrettyPrinter()
      .writeValueAsString(respObj)
    );

    httpResp.flushBuffer();
  }

  static ObjectNode failureToJson(OpenApiValidationFailure item, ObjectMapper objectMapper) {
    ObjectNode itemJson = objectMapper.createObjectNode();
    itemJson.put("dataLocation", item.describeInstanceLocation());
    String schemaLocation = item.describeSchemaLocation();
    int openapiDirIndex = schemaLocation.lastIndexOf("openapi/");
    if (openapiDirIndex >= 0) {
      itemJson.put("schemaLocation", schemaLocation.substring(openapiDirIndex));
    } else {
      itemJson.put("schemaLocation", schemaLocation);
    }
    if (item instanceof OpenApiValidationFailure.SchemaValidationFailure) {
      OpenApiValidationFailure.SchemaValidationFailure schemaValidationFailure =
        (OpenApiValidationFailure.SchemaValidationFailure) item;
      itemJson.put("dynamicPath", schemaValidationFailure.getFailure().getDynamicPath().getPointer().toString());
    }
    itemJson.put("message", item.getMessage());
    return itemJson;
  }
}

class RFC9457FailureSender
  implements ValidationFailureSender {

  private final String typeAttributeValue;

  public RFC9457FailureSender() {
    this("https://erosb.github.io/kappa/request-validation-failure");
  }

  public RFC9457FailureSender(String typeAttributeValue) {
    this.typeAttributeValue = requireNonNull(typeAttributeValue);
  }

  @Override
  public void send(ValidationException ex, HttpServletResponse httpResp)
    throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode respObj = objectMapper.createObjectNode();
    ArrayNode errors = objectMapper.createArrayNode();
    respObj.put("type", typeAttributeValue);
    respObj.put("status", 400);
    respObj.put("title", "Validation failure");
    respObj.put("detail", ex.getMessage());
    ex.results().forEach(item -> {
      errors.add(DefaultValidationFailureSender.failureToJson(item, objectMapper));
    });
    respObj.put("errors", errors);

    httpResp.setStatus(400);
    httpResp.getWriter().print(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(respObj));
    httpResp.flushBuffer();
  }
}
