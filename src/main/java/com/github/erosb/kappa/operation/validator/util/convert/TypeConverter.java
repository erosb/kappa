package com.github.erosb.kappa.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public final class TypeConverter {
  private static final TypeConverter INSTANCE = new TypeConverter();

  private TypeConverter() {
  }

  public static TypeConverter instance() {
    return INSTANCE;
  }

  public JsonNode convertObject(final OAIContext context,
                                final Schema schema,
                                final Map<String, Object> content) {
    if (schema == null || content == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    Map<String, Schema> properties = schema.getProperties();
    System.out.println("properties = " + properties);
    if (properties == null || properties.isEmpty()) {
      return JsonNodeFactory.instance.nullNode();
    }

    ObjectNode convertedContent = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Schema> entry : properties.entrySet()) {
      String entryKey = entry.getKey();

      if (!content.containsKey(entryKey)) {
        continue;
      }

      Object value = content.get(entryKey);

      Schema flatSchema = entry.getValue().getFlatSchema(context);
      switch (flatSchema.getSupposedType(context)) {
        case OAI3SchemaKeywords.TYPE_OBJECT:
          convertedContent.set(entryKey, convertObject(context, flatSchema, castMap(value)));
          break;
        case OAI3SchemaKeywords.TYPE_ARRAY:
          convertedContent.set(entryKey, convertArray(context, flatSchema.getItemsSchema(), castList(value)));
          break;
        default:
          convertedContent.set(entryKey, convertPrimitive(context, flatSchema, value));
          break;
      }
    }

    return convertedContent;
  }

  public JsonNode convertArray(final OAIContext context,
                               final Schema schema,
                               final Collection<Object> content) {
    if (schema == null) {
      return TreeUtil.json.convertValue(content, JsonNode.class);
    }
    if (content == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    ArrayNode convertedContent = JsonNodeFactory.instance.arrayNode();

    Schema flatSchema = schema.getFlatSchema(context);
    System.out.println("supposed type in convertArray(): " + flatSchema.getSupposedType(context));
    switch (flatSchema.getSupposedType(context)) {
      case OAI3SchemaKeywords.TYPE_OBJECT:
        for (Object value : content) {
          convertedContent.add(convertObject(context, flatSchema, castMap(value)));
        }
        break;
      case OAI3SchemaKeywords.TYPE_ARRAY:
        for (Object value : content) {
          convertedContent.add(convertArray(context, flatSchema.getItemsSchema(), castList(value)));
        }
        break;
      default:
        for (Object value : content) {
          convertedContent.add(convertPrimitive(context, flatSchema, value));
        }
        break;
    }

    return convertedContent;
  }

  public JsonNode convertPrimitive(final OAIContext context,
                                   final Schema schema,
                                   Object value) {

    if (value == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    if (schema == null) {
      return JsonNodeFactory.instance.textNode(value.toString());
    }

    try {
      Schema flatSchema = schema.getFlatSchema(context);
      switch (flatSchema.getSupposedType(context)) {
        case OAI3SchemaKeywords.TYPE_BOOLEAN:
          return JsonNodeFactory.instance.booleanNode(parseBoolean(value.toString()));
        case OAI3SchemaKeywords.TYPE_INTEGER:
          if (OAI3SchemaKeywords.FORMAT_INT32.equals(flatSchema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Integer.parseInt(value.toString()));
          } else if (OAI3SchemaKeywords.FORMAT_INT64.equals(flatSchema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Long.parseLong(value.toString()));
          } else {
            return JsonNodeFactory.instance.numberNode(new BigInteger(value.toString()));
          }
        case OAI3SchemaKeywords.TYPE_NUMBER:
          if (OAI3SchemaKeywords.FORMAT_FLOAT.equals(flatSchema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Float.parseFloat(value.toString()));
          } else if (OAI3SchemaKeywords.FORMAT_DOUBLE.equals(flatSchema.getFormat())) {
            return JsonNodeFactory.instance.numberNode(Double.parseDouble(value.toString()));
          } else {
            return JsonNodeFactory.instance.numberNode(new BigDecimal(value.toString()));
          }
        case OAI3SchemaKeywords.TYPE_STRING:
        default:
          return JsonNodeFactory.instance.textNode(value.toString());
      }
    } catch (IllegalArgumentException ex) {
      return JsonNodeFactory.instance.textNode(value.toString());
    }
  }

  /**
   * Parse boolean with exception if the value is not a boolean at all.
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
