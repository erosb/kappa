package org.openapi4j.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.jsonsKema.Validator;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationSeverity;

public class SkemaBackedJsonValidator implements JsonValidator {

  private final Schema schema;

  public SkemaBackedJsonValidator(JsonNode rawJson) {
    schema = new SchemaLoader(rawJson.toPrettyString()).load();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<?> validation) {
    System.out.println("ret bool?");
    IJsonValue jsonValue = new JsonParser(valueNode.toPrettyString()).parse();
//    validation.
    ValidationFailure validate = Validator.forSchema(schema).validate(jsonValue);
    if (validate != null) {
      validation.add(new ValidationResult(ValidationSeverity.ERROR, 0, validate.getMessage()));
    }
    return validate == null;
  }

  @Override
  public void validate(JsonNode valueNode)
    throws ValidationException {
    System.out.println("thro exc?");
    ValidationData<?> validation = new ValidationData<>();
    validate(valueNode, validation);

//    if (!validation.isValid()) {
      throw new ValidationException("msgs", validation.results());
//    }
  }
}
