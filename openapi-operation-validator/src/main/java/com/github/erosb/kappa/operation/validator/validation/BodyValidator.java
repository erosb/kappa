package com.github.erosb.kappa.operation.validator.validation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.kappa.core.model.v3.OAI3;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.URIFactory;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.schema.validator.JsonValidator;
import com.github.erosb.kappa.schema.validator.SkemaBackedJsonValidator;
import com.github.erosb.kappa.schema.validator.ValidationContext;
import com.github.erosb.kappa.schema.validator.ValidationData;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;

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
    } catch (JsonParseException ex) {
      validation.add(OpenApiValidationFailure.unparseableRequestBody(ex.getMessage()));
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
  }
}
