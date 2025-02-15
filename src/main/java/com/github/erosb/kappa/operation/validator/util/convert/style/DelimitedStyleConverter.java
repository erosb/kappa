package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.MultiStringMap;
import com.github.erosb.kappa.core.util.StringUtil;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.util.*;
import java.util.regex.Pattern;

class DelimitedStyleConverter extends FlatStyleConverter {
  protected final String delimiter;

  DelimitedStyleConverter(String delimiter) {
    this.delimiter = delimiter;
  }

  public JsonNode convert(OAIContext context,
                          AbsParameter<?> param,
                          String paramName,
                          MultiStringMap<String> paramPairs,
                          List<String> visitedParams) {

    Collection<String> paramValues = paramPairs.get(paramName);

    if (paramValues == null) {
      return null;
    }

    visitedParams.add(paramName);

    // In case of single value is null
    String paramValue
      = paramValues.size() == 1
      ? paramValues.iterator().next()
      : String.join(delimiter, paramValues);

    return convert(context, param, paramName, paramValue);
  }

  @Override
  public JsonNode convert(OAIContext context, AbsParameter<?> param, String paramName, String paramValue) {
    if (!OAI3SchemaKeywords.TYPE_ARRAY.equals(param.getSchema().getSupposedType(context))) {
      // delimited parameter cannot be an object or primitive
      return null;
    }

    List<String> values = StringUtil.tokenize(paramValue, Pattern.quote(delimiter), false, false);

    List<String> arrayValues = new ArrayList<>();

    for (String value : values) {
      if (param.isExplode()) {
        arrayValues.add(value);
      } else {
        arrayValues.addAll(StringUtil.tokenize(value, Pattern.quote(delimiter), false, false));
      }
    }

    Map<String, Object> paramValues = new HashMap<>();

    paramValues.put(paramName, arrayValues);

    return convert(context, param, paramName, paramValues);
  }
}
