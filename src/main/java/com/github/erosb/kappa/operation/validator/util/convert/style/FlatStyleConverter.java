package com.github.erosb.kappa.operation.validator.util.convert.style;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

abstract class FlatStyleConverter implements StyleConverter {
  Map<String, Object> getParameterValues(OAIContext context,
                                         AbsParameter<?> param,
                                         String paramName,
                                         String rawValue,
                                         String splitPattern) {
    if (rawValue == null) {
      return null;
    }

    Map<String, Object> values = new HashMap<>();

    if (OAI3SchemaKeywords.TYPE_OBJECT.equals(param.getSchema().getSupposedType(context))) {
      System.out.println("object");
      if (param.isExplode()) {
        handleExplodedObject(param, splitPattern, rawValue, values);
      } else {
        handleNotExplodedObject(param, splitPattern, rawValue, values);
      }
    } else if (OAI3SchemaKeywords.TYPE_ARRAY.equals(param.getSchema().getSupposedType(context))) {
      values.put(paramName, Arrays.asList(rawValue.split(splitPattern)));
    } else {
      values.put(paramName, rawValue);
    }

    return values;
  }

  private void handleExplodedObject(AbsParameter<?> param, String splitPattern, String rawValue, Map<String, Object> values) {
    System.out.println("exploded");
    Scanner scanner = new Scanner(rawValue);
    scanner.useDelimiter(splitPattern);
    while (scanner.hasNext()) {
      String[] propEntry = scanner.next().split("=");
      if (propEntry.length == 2 && param.getSchema().hasProperty(propEntry[0])) {
        values.put(propEntry[0], propEntry[1]);
      }
    }
    scanner.close();
  }

  private void handleNotExplodedObject(AbsParameter<?> param, String splitPattern, String rawValue, Map<String, Object> values) {
    String[] splitValues = rawValue.split(splitPattern);
    if (splitValues.length % 2 == 0) {
      int i = 0;
      while (i < splitValues.length) {
        if (param.getSchema().hasProperty(splitValues[i])) {
          values.put(splitValues[i++], splitValues[i++]);
        } else {
          i = i + 2;
        }
      }
    }
  }
}
