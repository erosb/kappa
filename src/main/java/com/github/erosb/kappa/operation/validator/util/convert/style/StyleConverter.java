package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.operation.validator.util.convert.TypeConverter;

import java.util.Collection;
import java.util.Map;

interface StyleConverter {
  JsonNode convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue);

  @SuppressWarnings("unchecked")
  default JsonNode convert(OAIContext context, AbsParameter<?> param, String paramName, Map<String, Object> paramValues) {
    if (paramValues == null || paramValues.size() == 0) {
      return null;
    }

    String style = param.getSchema().getSupposedType(context);
    Schema schema = param.getSchema();
    if (OAI3SchemaKeywords.TYPE_OBJECT.equals(style)) {
      return TypeConverter.instance().convertObject(context, schema, paramValues);
    } else if (OAI3SchemaKeywords.TYPE_ARRAY.equals(style)) {
      Object value = paramValues.get(paramName);
      return (value instanceof Collection)
        ? TypeConverter.instance().convertArray(context, schema.getItemsSchema(), (Collection<Object>) value)
        : JsonNodeFactory.instance.nullNode();
    } else {
      return TypeConverter.instance().convertPrimitive(context, schema, paramValues.get(paramName));
    }
  }
}
