package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonArray;
import com.github.erosb.jsonsKema.JsonString;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatrixStyleConverter extends FlatStyleConverter {
  private static final Pattern PREFIXED_SEMICOLON_NAME_REGEX = Pattern.compile("(?:;)([^;]+)(?:=)([^;]*)");

  private static final MatrixStyleConverter INSTANCE = new MatrixStyleConverter();

  private MatrixStyleConverter() {
  }

  public static MatrixStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public IJsonValue convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    final Map<String, IJsonValue> paramValues;
    paramValues = getValues(context, param, paramName, rawValue, param.isExplode() ? ";" : ",");

    return convert(context, param, paramName, paramValues);
  }

  private Map<String, IJsonValue> getValues(OAIContext context, AbsParameter<?> param, String paramName, String rawValue,
                                            String splitPattern) {
    String type = param.getSchema().getSupposedType(context);
    if (OAI3SchemaKeywords.TYPE_OBJECT.equals(type)) {
      return getObjectValues(context, param, paramName, rawValue, splitPattern);
    } else {
      Map<String, IJsonValue> values = new HashMap<>();

      if (OAI3SchemaKeywords.TYPE_ARRAY.equals(type)) {
        List<JsonString> arrayValues = getArrayValues(param, rawValue, splitPattern);
        if (arrayValues != null && !arrayValues.isEmpty()) {
          values.put(paramName, new JsonArray(arrayValues));
        }
      } else {
        Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);
        if (matcher.matches()) {
          values.put(matcher.group(1), new JsonString(matcher.group(2)));
        }
      }

      return values;
    }
  }

  private Map<String, IJsonValue> getObjectValues(OAIContext context,
                                                  AbsParameter<?> param,
                                                  String paramName,
                                                  String rawValue,
                                                  String splitPattern) {

    Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);

    if (param.isExplode()) {
      Map<String, IJsonValue> values = new HashMap<>();
      while (matcher.find()) {
        values.put(matcher.group(1), new JsonString(matcher.group(2)));
      }
      return values;
    } else {
      return (matcher.find())
        ? getParameterValues(context, param, paramName, matcher.group(2), splitPattern)
        : null;
    }
  }

  private List<JsonString> getArrayValues(AbsParameter<?> param, String rawValue, String splitPattern) {
    Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);

    if (param.isExplode()) {
      List<JsonString> arrayValues = new ArrayList<>();
      while (matcher.find()) {
        arrayValues.add(new JsonString(matcher.group(2)));
      }
      return arrayValues;
    } else {
      return matcher.matches()
        ? Arrays.asList(matcher.group(2).split(splitPattern)).stream().map(JsonString::new).collect(Collectors.toList())
        : null;
    }
  }
}
