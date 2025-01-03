package org.perf.check.schema;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.schema.validator.ValidationData;

class OpenApi4j implements JsonValidator {

  OpenApi4j(JsonNode schema) throws ResolutionException {
//    schemaValidator = new SchemaValidator("schemas", schema);
  }

  @Override
  public String validate(JsonNode data) {
    ValidationData<Void> validation = new ValidationData<>();
//    schemaValidator.validate(data, validation);
    if (!validation.isValid()) {
      return validation.toString();
    }

    return null;
  }

  @Override
  public String getVersion() {
    return OpenApiValidationFailure.class.getPackage().getImplementationVersion();
  }
}
