package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.util.Map;

public class LabelStyleConverter extends FlatStyleConverter {
  private static final LabelStyleConverter INSTANCE = new LabelStyleConverter();

  private LabelStyleConverter() {}

  public static LabelStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    final Map<String, Object> paramValues;
    paramValues = getParameterValues(context, param, paramName, rawValue.substring(1), param.isExplode() ? "\\." : ",");

    return convert(context, param, paramName, paramValues);
  }
}
