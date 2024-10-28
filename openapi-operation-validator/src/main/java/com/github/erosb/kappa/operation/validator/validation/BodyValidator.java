package com.github.erosb.kappa.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonParseException;
import com.github.erosb.kappa.core.model.v3.OAI3;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.URIFactory;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.schema.validator.SKemaBackedJsonValidator;
import com.github.erosb.kappa.schema.validator.ValidationContext;
import com.github.erosb.kappa.schema.validator.ValidationData;

import java.net.URISyntaxException;

class BodyValidator {

  private final ValidationContext<OAI3> context;
  private final MediaType mediaType;
  private final SKemaBackedJsonValidator validator;
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
      IJsonValue jsonBody = body.contentAsNode(rawContentType, uriFactory.requestBody());
      validator.validate(jsonBody, validation);
    } catch (JsonParseException ex) {
      validation.add(OpenApiValidationFailure.unparseableRequestBody(ex));
    }
  }

  private SKemaBackedJsonValidator initValidator() {
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
      return new SKemaBackedJsonValidator(rawJson, context.getContext().getBaseUrl().toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
