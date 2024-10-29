package com.github.erosb.kappa.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.jsonsKema.FormatValidationPolicy;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.jsonsKema.Validator;
import com.github.erosb.jsonsKema.ValidatorConfig;

import java.net.URI;

public class SKemaBackedJsonValidator
  implements JsonValidator {

  private final Schema schema;

  public SKemaBackedJsonValidator(JsonNode rawJson, URI documentSource) {
    String schemaJsonString = rawJson.toPrettyString();
    schema = new SchemaLoader(new JsonParser(schemaJsonString, documentSource).parse())
      .load();
  }

  public boolean validate(IJsonValue jsonValue, ValidationData<?> validation) {
    ValidationFailure failure = Validator.create(schema, new ValidatorConfig(FormatValidationPolicy.ALWAYS)).validate(jsonValue);
    if (failure != null) {
      validation.add(failure);
      return false;
    }
    return true;
  }

  /**
   *
   * @deprecated use validate(jsonValue, validation) instead
   */
  @Override
  @Deprecated
  public boolean validate(JsonNode valueNode, URI documentSource, ValidationData<?> validation) {
    String jsonString = valueNode.toPrettyString();
    IJsonValue jsonValue = new JsonParser(jsonString, documentSource).parse();
    return validate(jsonValue, validation);
  }
}
