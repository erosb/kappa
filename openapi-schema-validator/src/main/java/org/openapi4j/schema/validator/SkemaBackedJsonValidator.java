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
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;

import java.net.URI;
import java.util.Arrays;

public class SkemaBackedJsonValidator implements JsonValidator {

  private final Schema schema;

  public SkemaBackedJsonValidator(JsonNode rawJson, URI documentSource) {
    String schemaJsonString = rawJson.toPrettyString();
    schema = new SchemaLoader(new JsonParser(schemaJsonString, documentSource).parse())
      .load();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<?> validation) {
    String jsonString = valueNode.toPrettyString();
    IJsonValue jsonValue = new JsonParser(jsonString).parse();
    ValidationFailure validate = Validator.forSchema(schema).validate(jsonValue);
    if (validate != null) {
      collectLeafValidationFailures(validate, validation);
    }
    return validate == null;
  }

  private void collectLeafValidationFailures(ValidationFailure rootFailure, ValidationData<?> validation) {
    if (rootFailure.getCauses().isEmpty()) {
      ValidationResult res = new ValidationResult(ValidationSeverity.ERROR, 0, rootFailure.getMessage());
      ValidationResults rs = new ValidationResults();
      rs.add(res);
      validation.add(Arrays.asList(
        new ValidationResults.CrumbInfo(rootFailure.getSchema().getLocation().toString(), true),
        new ValidationResults.CrumbInfo(rootFailure.getInstance().getLocation().toString(), false)
      ), rs);
    } else {
      rootFailure.getCauses().forEach(cause -> collectLeafValidationFailures(cause, validation));
    }
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
