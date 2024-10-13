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

import java.net.URI;

public class SkemaBackedJsonValidator implements JsonValidator {

  private final Schema schema;

  public SkemaBackedJsonValidator(JsonNode rawJson, URI documentSource) {
    String schemaJsonString = rawJson.toPrettyString();
    System.out.println("init SkemaBackedValidator: ");
    System.out.println(schemaJsonString);
    schema = new SchemaLoader(new JsonParser(schemaJsonString, documentSource).parse())
      .load();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<?> validation) {
    String jsonString = valueNode.toPrettyString();
//    System.out.println(jsonString);
    IJsonValue jsonValue = new JsonParser(jsonString).parse();
    ValidationFailure validate = Validator.forSchema(schema).validate(jsonValue);
    if (validate != null) {
      validation.add(new ValidationResult(ValidationSeverity.ERROR, 0, validate.getMessage()));
    }
    return validate == null;
  }

  @Override
  public void validate(JsonNode valueNode)
    throws ValidationException {
    ValidationData<?> validation = new ValidationData<>();
    validate(valueNode, validation);

//    if (!validation.isValid()) {
      throw new ValidationException("msgs", validation.results());
//    }
  }
}
