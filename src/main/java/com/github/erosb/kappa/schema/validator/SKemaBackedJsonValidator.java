package com.github.erosb.kappa.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.FormatValidationPolicy;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.jsonsKema.Validator;
import com.github.erosb.jsonsKema.ValidatorConfig;
import com.github.erosb.kappa.core.model.v3.OAI3;
import com.github.erosb.kappa.core.util.TreeUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SKemaBackedJsonValidator
  implements JsonValidator {

  private static URI toURI(URL url) {
    try {
      return url.toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static URI rewriteProbableJarUrl(URI uri)
    throws URISyntaxException {
    String uriString = uri.toString();
    if (uriString.startsWith("jar:nested:") && uriString.indexOf('!') >= 0) {
      String fixedUri = "classpath:/" + uriString.substring(uriString.lastIndexOf('!') + 1);
      return new URI(fixedUri);
    }
    return uri;
  }

  private final Schema schema;

  public SKemaBackedJsonValidator(com.github.erosb.kappa.parser.model.v3.Schema schema, ValidationContext<OAI3> context) {
    this(schema, context, toURI(context.getContext().getBaseUrl()));
  }

  public SKemaBackedJsonValidator(com.github.erosb.kappa.parser.model.v3.Schema schema, ValidationContext<OAI3> context,
                                  URI baseURI) {
    JsonNode rawJson = TreeUtil.json.convertValue(schema, JsonNode.class);
    if (rawJson instanceof ObjectNode) {
      ObjectNode obj = (ObjectNode) rawJson;
      obj.set("components", context.getContext().getBaseDocument().get("components"));
    }
    try {
      this.schema = new SchemaLoader(new JsonParser(
        rawJson.toPrettyString(),
        rewriteProbableJarUrl(baseURI)
      ).parse()).load();
      schema.setSkema(this.schema);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean validate(IJsonValue jsonValue, ValidationData<?> validation) {
    System.out.println("validate " + jsonValue + " against schema ");
    ValidationFailure failure = Validator.create(schema, new ValidatorConfig(FormatValidationPolicy.ALWAYS)).validate(jsonValue);
    System.out.println("   -> failure = " + failure);
    if (failure != null) {
      validation.add(failure);
      return false;
    }
    return true;
  }

  /**
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
