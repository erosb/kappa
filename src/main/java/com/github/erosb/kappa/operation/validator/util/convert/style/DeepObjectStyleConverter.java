package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.IJsonObject;
import com.github.erosb.jsonsKema.IJsonString;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonObject;
import com.github.erosb.jsonsKema.JsonString;
import com.github.erosb.jsonsKema.JsonValue;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.MultiStringMap;
import com.github.erosb.kappa.core.util.StringUtil;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.operation.validator.util.convert.TypeConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepObjectStyleConverter {
  private static final DeepObjectStyleConverter INSTANCE = new DeepObjectStyleConverter();

  private DeepObjectStyleConverter() {
  }

  public static DeepObjectStyleConverter instance() {
    return INSTANCE;
  }

  public IJsonValue convert(OAIContext context,
                            AbsParameter<?> param,
                            String paramName,
                            MultiStringMap<String> paramPairs,
                            List<String> visitedParams) {

    Map<IJsonString, IJsonValue> result = new HashMap<>();
    Schema propSchema = param.getSchema();
    String type = propSchema.getSupposedType(context);

    for (Map.Entry<String, Collection<String>> valueEntry : paramPairs.entrySet()) {
      String propPath = valueEntry.getKey();

      if (propPath.startsWith(paramName + "[")) {
        // tokenize
        List<String> properties = StringUtil.tokenize(propPath, "\\[|\\]", true, true);
        if (properties.size() == 2) {
          String propName = properties.get(1);

          // Convert value or get string representation
          IJsonValue value = TypeConverter.instance().convertPrimitive(
            context,
            propSchema.getProperty(propName),
            valueEntry.getValue().stream().findFirst().orElse(null));

          result.put(new JsonString(propName), (JsonValue) value);

          visitedParams.add(propPath);
        }
      } else if (propPath.equals(paramName) && OAI3SchemaKeywords.TYPE_OBJECT.equals(type)) {
        // propPath is malformed, we still invalidate the paramName
        visitedParams.add(propPath);
      }
    }

    return new JsonObject(result);
  }
}
