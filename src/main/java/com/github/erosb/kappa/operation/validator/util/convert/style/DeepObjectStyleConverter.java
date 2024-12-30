package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.MultiStringMap;
import com.github.erosb.kappa.core.util.StringUtil;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.operation.validator.util.convert.TypeConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeepObjectStyleConverter {
  private static final DeepObjectStyleConverter INSTANCE = new DeepObjectStyleConverter();

  private DeepObjectStyleConverter() {
  }

  public static DeepObjectStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(OAIContext context,
                          AbsParameter<?> param,
                          String paramName,
                          MultiStringMap<String> paramPairs,
                          List<String> visitedParams) {

    ObjectNode result = JsonNodeFactory.instance.objectNode();
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
          JsonNode value = TypeConverter.instance().convertPrimitive(
            context,
            propSchema.getProperty(propName),
            valueEntry.getValue().stream().findFirst().orElse(null));

          result.set(propName, value);

          visitedParams.add(propPath);
        }
      } else if (propPath.equals(paramName) && OAI3SchemaKeywords.TYPE_OBJECT.equals(type)) {
        // propPath is malformed, we still invalidate the paramName
        visitedParams.add(propPath);
      }
    }

    return result;
  }
}
