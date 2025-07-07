package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ValidationFailureSender {

  static ValidationFailureSender defaultSender() {
    return new DefaultValidationFailureSender();
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
}
