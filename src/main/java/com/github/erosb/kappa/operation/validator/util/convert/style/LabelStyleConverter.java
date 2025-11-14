package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonValueKt;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.jsonsKema.UnknownSource;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.util.Map;

public class LabelStyleConverter extends FlatStyleConverter {
  private static final LabelStyleConverter INSTANCE = new LabelStyleConverter(UnknownSource.INSTANCE);

  private LabelStyleConverter(SourceLocation location) {
    super(location);
  }

  public static LabelStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public IJsonValue convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    final Map<String, IJsonValue> paramValues;
    paramValues = getParameterValues(context, param, paramName, rawValue.substring(1), param.isExplode() ? "\\." : ",");

    return convert(context, param, paramName, paramValues);
  }
}
