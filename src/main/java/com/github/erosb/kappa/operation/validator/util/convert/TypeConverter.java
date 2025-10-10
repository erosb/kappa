package com.github.erosb.kappa.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.IJsonString;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonArray;
import com.github.erosb.jsonsKema.JsonNull;
import com.github.erosb.jsonsKema.JsonObject;
import com.github.erosb.jsonsKema.JsonString;
import com.github.erosb.jsonsKema.JsonValue;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypeConverter {
  private static final TypeConverter INSTANCE = new TypeConverter();

  private TypeConverter() {
  }

  public static TypeConverter instance() {
    return INSTANCE;
  }

  public IJsonValue convertObject(final OAIContext context,
                                  final Schema schema,
                                  final Map<String, Object> content) {
    if (schema == null || content == null) {
      return new JsonNull();
    }

    Map<String, Schema> properties = schema.getProperties();
    if (properties == null || properties.isEmpty()) {
      return new JsonNull();
    }

    Map<IJsonString, IJsonValue> convertedContent = new HashMap<>();

    for (Map.Entry<String, Schema> entry : properties.entrySet()) {
      String entryKey = entry.getKey();

      if (!content.containsKey(entryKey)) {
        continue;
      }

      Object value = content.get(entryKey);

      Schema flatSchema = entry.getValue();
      switch (flatSchema.getSupposedType(context)) {
        case OAI3SchemaKeywords.TYPE_OBJECT:
          convertedContent.put(new JsonString(entryKey), convertObject(context, flatSchema, castMap(value)));
          break;
        case OAI3SchemaKeywords.TYPE_ARRAY:
          convertedContent.put(new JsonString(entryKey), convertArray(context, flatSchema.getItemsSchema(), castList(value)));
          break;
        default:
          convertedContent.put(new JsonString(entryKey), convertPrimitive(context, flatSchema, value));
          break;
      }
    }

    return new JsonObject(convertedContent);
  }

  public IJsonValue convertArray(final OAIContext context,
                                 final Schema schema,
                                 final Collection<Object> content) {

    if (schema == null || content == null) {
      return new JsonNull();
    }

    List<IJsonValue> convertedContent = new ArrayList<>();

    switch (schema.getSupposedType(context)) {
      case OAI3SchemaKeywords.TYPE_OBJECT:
        for (Object value : content) {
          convertedContent.add(convertObject(context, schema, castMap(value)));
        }
        break;
      case OAI3SchemaKeywords.TYPE_ARRAY:
        for (Object value : content) {
          convertedContent.add(convertArray(context, schema.getItemsSchema(), castList(value)));
        }
        break;
      default:
        for (Object value : content) {
          convertedContent.add(convertPrimitive(context, schema, value));
        }
        break;
    }

    return new JsonArray(convertedContent);
  }

  public IJsonValue convertPrimitive(final OAIContext context,
                                     final Schema schema,
                                     Object value) {
    if (value == null) {
      return new JsonNull();
    }
    return new JsonString(value.toString());
  }

  /**
   * Parse boolean with exception if the value is not a boolean at all.
   *
   * @param value The boolean value to parse.
   * @return If the value is not a boolean representation.
   */
  private boolean parseBoolean(String value) {
    value = value.trim().toLowerCase();

    if ("true".equals(value)) {
      return true;
    } else if ("false".equals(value)) {
      return false;
    }

    throw new IllegalArgumentException(value);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> castMap(Object obj) {
    try {
      return (Map<String, Object>) obj;
    } catch (ClassCastException ex) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<Object> castList(Object obj) {
    try {
      return (Collection<Object>) obj;
    } catch (ClassCastException ex) {
      return null;
    }
  }
}
