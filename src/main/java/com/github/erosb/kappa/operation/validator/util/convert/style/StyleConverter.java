package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.erosb.jsonsKema.IJsonArray;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonNull;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.operation.validator.util.convert.TypeConverter;

import java.util.Collection;
import java.util.Map;

interface StyleConverter {
  IJsonValue convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue);

  @SuppressWarnings("unchecked")
  default IJsonValue convert(OAIContext context, AbsParameter<?> param, String paramName, Map<String, IJsonValue> paramValues) {
    if (paramValues == null || paramValues.isEmpty()) {
      return null;
    }

    String style = param.getSchema().getSupposedType(context);
    Schema schema = param.getSchema();
    if (OAI3SchemaKeywords.TYPE_OBJECT.equals(style)) {
      return TypeConverter.instance().convertObject(context, schema, paramValues);
    } else if (OAI3SchemaKeywords.TYPE_ARRAY.equals(style)) {
      IJsonValue value = paramValues.get(paramName);
      return (value instanceof IJsonArray) ? value : new JsonNull();
    } else {
      return paramValues.get(paramName);
    }
  }
}
