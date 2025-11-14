package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.util.Map;

public class SimpleStyleConverter extends FlatStyleConverter {
  private static final SimpleStyleConverter INSTANCE = new SimpleStyleConverter();

  private SimpleStyleConverter() {
  }

  public static SimpleStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public IJsonValue convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue) {
    final Map<String, IJsonValue> paramValues = getParameterValues(context, param, paramName, rawValue, ",");
    return convert(context, param, paramName, paramValues);
  }
}
