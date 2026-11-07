package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonArray;
import com.github.erosb.jsonsKema.JsonString;
import com.github.erosb.jsonsKema.JsonValue;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

abstract class FlatStyleConverter extends StyleConverter {

  private final SourceLocation location;

  FlatStyleConverter(SourceLocation location) {
    this.location = requireNonNull(location);
  }

  JsonString jsonString(String value) {
    return new JsonString(value, location);
  }

  JsonArray jsonArray(List<JsonString> elems) {
    return new JsonArray(elems, location);
  }

  JsonArray jsonArrayOfValues(List<JsonValue> elems) {
    return new JsonArray(elems, location);
  }

  Map<String, IJsonValue> getParameterValues(OAIContext context,
                                             AbsParameter<?> param,
                                             String paramName,
                                             String rawValue,
                                             String splitPattern) {
    if (rawValue == null) {
      return null;
    }

    Map<String, IJsonValue> values = new HashMap<>();

    if (OAI3SchemaKeywords.TYPE_OBJECT.equals(param.getSchema().getSupposedType(context))) {
      if (param.isExplode()) {
        handleExplodedObject(param, splitPattern, rawValue, values);
      } else {
        handleNotExplodedObject(param, splitPattern, rawValue, values);
      }
    } else if (OAI3SchemaKeywords.TYPE_ARRAY.equals(param.getSchema().getSupposedType(context))) {
      values.put(paramName, jsonArray(Arrays.stream(rawValue.split(splitPattern))
        .map(JsonString::new)
        .collect(Collectors.toList())));
    } else {
      values.put(paramName, jsonString(rawValue));
    }

    return values;
  }

  private void handleExplodedObject(AbsParameter<?> param, String splitPattern, String rawValue, Map<String, IJsonValue> values) {
    Scanner scanner = new Scanner(rawValue);
    scanner.useDelimiter(splitPattern);
    while (scanner.hasNext()) {
      String[] propEntry = scanner.next().split("=");
      if (propEntry.length == 2 && param.getSchema().hasProperty(propEntry[0])) {
        values.put(propEntry[0], jsonString(propEntry[1]));
      }
    }
    scanner.close();
  }

  private void handleNotExplodedObject(AbsParameter<?> param, String splitPattern, String rawValue,
                                       Map<String, IJsonValue> values) {
    String[] splitValues = rawValue.split(splitPattern);
    if (splitValues.length % 2 == 0) {
      int i = 0;
      while (i < splitValues.length) {
        if (param.getSchema().hasProperty(splitValues[i])) {
          values.put(splitValues[i++], jsonString(splitValues[i++]));
        } else {
          i = i + 2;
        }
      }
    }
  }
}
