package org.openapi4j.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.URIFactory;
import org.openapi4j.operation.validator.model.impl.Body;
import org.openapi4j.parser.model.v3.MediaType;
import org.openapi4j.parser.model.v3.Schema;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.SkemaBackedJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;

class BodyValidator {

  private final ValidationContext<OAI3> context;
  private final MediaType mediaType;
  private final JsonValidator validator;
  private final URIFactory uriFactory = new URIFactory();

  BodyValidator(ValidationContext<OAI3> context, MediaType mediaType) {
    this.context = context;
    this.mediaType = mediaType;

    validator = initValidator();
  }

  void validate(final Body body,
                final String rawContentType,
                final ValidationData<?> validation) {

    if (validator == null) {
      return; // No schema specified for body
    } else if (body == null) {
      validator.validate(JsonNodeFactory.instance.nullNode(), uriFactory.requestBody(), validation);
      return;
    }

    try {
      JsonNode jsonBody = body.getContentAsNode(context.getContext(), mediaType, rawContentType);
      validator.validate(jsonBody, uriFactory.requestBody(), validation);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private JsonValidator initValidator() {
    if (mediaType == null || mediaType.getSchema() == null) {
      return null;
    }

    Schema copy = mediaType.getSchema().copy();
    JsonNode rawJson = TreeUtil.json.convertValue(copy, JsonNode.class);
    if (rawJson instanceof ObjectNode) {
      ObjectNode obj = (ObjectNode) rawJson;
      obj.set("components", context.getContext().getBaseDocument().get("components"));
    }
    try {
      return new SkemaBackedJsonValidator(rawJson, context.getContext().getBaseUrl().toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    //    return new SchemaValidator(
    //      context,
    //      BODY,
    //      TreeUtil.json.convertValue(mediaType.getSchema().copy(), JsonNode.class));
  }
}
