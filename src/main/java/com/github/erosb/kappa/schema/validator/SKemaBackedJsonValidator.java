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
import java.net.URISyntaxException;

public class SKemaBackedJsonValidator
  implements JsonValidator {

  static URI rewriteProbableJarUrl(URI uri)
    throws URISyntaxException {
    String uriString = uri.toString();
    if (uriString.startsWith("jar:nested:") && uriString.indexOf('!') >= 0) {
      String fixedUri = "classpath:/" + uriString.substring(uriString.lastIndexOf('!') + 1);
      return new URI(fixedUri);
    }
    return uri;
  }

  private final Schema schema;

  public SKemaBackedJsonValidator(JsonNode rawJson, URI documentSource) {
    String schemaJsonString = rawJson.toPrettyString();
      try {
          schema = new SchemaLoader(new JsonParser(
            schemaJsonString,
            rewriteProbableJarUrl(documentSource)
          ).parse()).load();
      } catch (URISyntaxException e) {
          throw new RuntimeException(e);
      }
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
