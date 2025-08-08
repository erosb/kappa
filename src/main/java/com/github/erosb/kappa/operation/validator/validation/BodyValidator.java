package com.github.erosb.kappa.operation.validator.validation;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonParseException;
import com.github.erosb.kappa.core.model.v3.OAI3;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.OperationContextUriFactory;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.schema.validator.SKemaBackedJsonValidator;
import com.github.erosb.kappa.schema.validator.ValidationContext;
import com.github.erosb.kappa.schema.validator.ValidationData;

class BodyValidator {

  private final ValidationContext<OAI3> context;
  private final MediaType mediaType;
  private final SKemaBackedJsonValidator validator;
  private final OperationContextUriFactory uriFactory;

  BodyValidator(ValidationContext<OAI3> context, MediaType mediaType, OperationContextUriFactory uriFactory) {
    this.context = context;
    this.mediaType = mediaType;
    this.uriFactory = uriFactory;
    validator = initValidator();
  }

  void validate(Body body,
                String rawContentType,
                ValidationData<?> validation) {

    if (validator == null) {
      return; // No schema specified for body
    } else if (body == null) {
      validator.validate(JsonNodeFactory.instance.nullNode(), uriFactory.httpEntity(), validation);
      return;
    }

    try {
      IJsonValue jsonBody = body.contentAsNode(rawContentType, uriFactory.httpEntity());
      validator.validate(jsonBody, validation);
    } catch (JsonParseException ex) {
      validation.add(OpenApiValidationFailure.unparseableHttpEntity(ex, uriFactory.definitionHttpEntity()));
    }
  }

  private SKemaBackedJsonValidator initValidator() {
    if (mediaType == null || mediaType.getSchema() == null) {
      return null;
    }

    Schema copy = mediaType.getSchema().copy();
    return new SKemaBackedJsonValidator(copy, context);
  }
}
