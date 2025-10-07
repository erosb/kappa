package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.IJsonString;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonObject;
import com.github.erosb.jsonsKema.JsonString;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.MultiStringMap;
import com.github.erosb.kappa.core.util.StringUtil;
import com.github.erosb.kappa.operation.validator.util.convert.TypeConverter;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormStyleConverter {
  private static final FormStyleConverter INSTANCE = new FormStyleConverter();

  private FormStyleConverter() {
  }

  public static FormStyleConverter instance() {
    return INSTANCE;
  }

  public IJsonValue convert(OAIContext context,
                            AbsParameter<?> param,
                            String paramName,
                            MultiStringMap<String> paramPairs,
                            List<String> visitedParams) {

    if (paramPairs == null) {
      return null;
    }

    IJsonValue result;
    String type = param.getSchema().getSupposedType(context);
    if (OAI3SchemaKeywords.TYPE_ARRAY.equals(type)) {
      result = getArrayValues(context, param, paramPairs.get(paramName));
      visitedParams.add(paramName);
    } else if (OAI3SchemaKeywords.TYPE_OBJECT.equals(type)) {
      result = getObjectValues(context, param, paramName, paramPairs, visitedParams);
    } else {
      result = getPrimitiveValue(context, param, paramPairs.get(paramName));
      visitedParams.add(paramName);
    }
    return result;
  }

  private IJsonValue getArrayValues(OAIContext context, AbsParameter<?> param, Collection<String> paramValues) {
    if (paramValues == null) {
      return null;
    }

    List<Object> values = new ArrayList<>();
    if (param.isExplode()) {
      values.addAll(paramValues);
    } else {
      for (String paramValue : paramValues) {
        values.addAll(StringUtil.tokenize(paramValue, ",", false, false));
      }
    }
    return TypeConverter.instance().convertArray(context, param.getSchema().getItemsSchema(), values);
  }

  private IJsonValue getObjectValues(OAIContext context, AbsParameter<?> param, String paramName, MultiStringMap<String> values,
                                     List<String> visitedParams) {
    if (param.isExplode()) {
      return getExplodedObjectValues(context, param, values, visitedParams);
    } else {
      return getNotExplodedObjectValues(context, param, paramName, values, visitedParams);
    }
  }

  private IJsonValue getExplodedObjectValues(OAIContext context, AbsParameter<?> param, MultiStringMap<String> values,
                                             List<String> visitedParams) {
    Map<IJsonString, IJsonValue> result = new HashMap<>(param.getSchema().getProperties().size());

    for (Map.Entry<String, Schema> propEntry : param.getSchema().getProperties().entrySet()) {
      String propName = propEntry.getKey();
      Collection<String> paramValues = values.get(propName);

      if (paramValues != null) {
        IJsonValue value = TypeConverter.instance().convertPrimitive(
          context,
          propEntry.getValue(),
          getParamValue(paramValues));

        result.put(new JsonString(propName), value);

        visitedParams.add(propName);
      }
    }

    return result.isEmpty() ? null : new JsonObject(result);
  }

  private IJsonValue getNotExplodedObjectValues(OAIContext context, AbsParameter<?> param, String paramName,
                                                MultiStringMap<String> values, List<String> visitedParams) {
    Collection<String> paramValues = values.get(paramName);
    visitedParams.add(paramName);

    if (paramValues == null) {
      return null;
    }

    String value = getParamValue(paramValues);
    if (value == null) {
      return null;
    }

    ObjectNode result = JsonNodeFactory.instance.objectNode();

    List<String> arrayValues = StringUtil.tokenize(value, ",", false, false);
    int idx = 0;
    while (idx < arrayValues.size()) {
      String propName = arrayValues.get(idx++);
      String propValue = arrayValues.get(idx++);
      Schema propSchema = param.getSchema().getProperty(propName);

      result.set(propName, TypeConverter.instance().convertPrimitive(context, propSchema, propValue));
    }

    return result;
  }

  private IJsonValue getPrimitiveValue(OAIContext context, AbsParameter<?> param, Collection<String> paramValues) {
    if (paramValues == null) {
      return null;
    }

    return TypeConverter.instance().convertPrimitive(
      context,
      param.getSchema(),
      getParamValue(paramValues));
  }

  private String getParamValue(Collection<String> paramValues) {
    if (paramValues == null) {
      return null;
    }

    return paramValues.stream().filter(Objects::nonNull).findFirst().orElse(null);
  }
}
