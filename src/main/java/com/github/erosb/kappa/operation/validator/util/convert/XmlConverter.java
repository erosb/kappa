package com.github.erosb.kappa.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.github.erosb.jsonsKema.IJsonArray;
import com.github.erosb.jsonsKema.IJsonObject;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonArray;
import com.github.erosb.jsonsKema.JsonNull;
import com.github.erosb.jsonsKema.JsonParseException;
import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.Schema;
import org.json.JSONObject;
import org.json.XML;
import org.json.XMLParserConfiguration;
import com.github.erosb.kappa.parser.model.v3.Xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class XmlConverter {
  private static final XmlConverter INSTANCE = new XmlConverter();

  // Pattern to remove all namespace information
  private static final Pattern nsPattern
    = Pattern.compile("<(/)*" // begin XML chapter & slash ?
    + "\\w+:" // namespace prefix
    + "(\\w+)" // XML chapter
    + "((?:\\s+\\w+=\".+?\")*)" // XML attributes before namespace reference
    + "(?:\\s+\\w+:\\w+=\".+?\")?" // namespace reference
    + "((?:\\s+\\w+=\".+?\")*)" // XML attributes after namespace reference
    + "(\\s*/?)>"); // trailing slash ? & end XML chapter

  private static final String nsReplace = "<$1$2$3$4$5>";

  private XmlConverter() {
  }

  static XmlConverter instance() {
    return INSTANCE;
  }

  IJsonValue convert(OAIContext context, final Schema schema, String body) {
    return convert(
      context,
      schema,
      XML.toJSONObject(nsPattern.matcher(body).replaceAll(nsReplace), XMLParserConfiguration.KEEP_STRINGS));
  }

  private IJsonValue convert(OAIContext context, final Schema schema, final JSONObject xml) {
    if (xml.isEmpty()) {
      return new JsonNull();
    }

    IJsonObject content;
    try {
      content = new JsonParser(xml.toString()).parse().requireObject();
    } catch (JsonParseException e) {
      return new JsonNull();
    }
    // Specific case of xml2json mapping : Unwrap first key to match JSON content
    if (OAI3SchemaKeywords.TYPE_OBJECT.equals(schema.getSupposedType(context))) {
      content = content.getProperties().entrySet().iterator().next().getValue().requireObject();
    }

    return content;
  }

  private IJsonValue processNode(OAIContext context, final Schema schema, final IJsonValue node) {
    IJsonValue content = unwrap(context, schema, node.requireObject(), null);
    if (content == null) {
      return null;
    }

    if (OAI3SchemaKeywords.TYPE_ARRAY.equals(schema.getSupposedType(context))) {
      return parseArray(context, schema, content);
    } else if (OAI3SchemaKeywords.TYPE_OBJECT.equals(schema.getSupposedType(context))) {
      return parseObject(context, schema, content);
    } else {
      throw new UnsupportedOperationException("TODO");
//      return TypeConverter.instance().convertPrimitive(context, schema, content.asText());
    }
  }

  private IJsonValue parseArray(OAIContext context, final Schema schema, final IJsonValue node) {
    if (!(node instanceof IJsonArray)) {
      return new JsonNull();
    }

    List<IJsonValue> resultNode = new ArrayList<>();
    for (IJsonValue arrayItem : ((IJsonArray) node).getElements()) {
      resultNode.add(processNode(context, schema.getItemsSchema(), arrayItem));
    }

    return new JsonArray(resultNode);
  }

  private IJsonValue parseObject(OAIContext context, final Schema schema, final IJsonValue node) {
    if (!(node instanceof IJsonObject)) {
      return new JsonNull();
    }

    ObjectNode resultNode = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
      String entryKey = entry.getKey();
      Schema propSchema = entry.getValue();

//      JsonNode value = processNode(context, propSchema, unwrap(context, schema, node, entryKey));
//
//      if (value != null) {
//        resultNode.set(entryKey, value);
//      }
    }
    throw new UnsupportedOperationException("why do we need this?");
//    return resultNode;
  }

  private IJsonValue unwrap(OAIContext context, final Schema schema, final IJsonObject content, final String defaultKey) {
    Xml xmlConf = schema.getXml();

    if (OAI3SchemaKeywords.TYPE_ARRAY.equals(schema.getSupposedType(context))) {
      // is array wrapped ?
      if (xmlConf != null && xmlConf.isWrapped()) {
        if (xmlConf.getName() != null) {
          return getRenamedNode(xmlConf, content.get(xmlConf.getName()).requireObject(), xmlConf.getName());
        }

        // fallback for array as root element
        if (content.getProperties().size() == 1) {
          return content.getProperties().entrySet().iterator().next().getValue();
        } else {
          return new JsonNull();
        }
      }

      // is unwrapped array has a renamed node ?
      xmlConf = schema.getItemsSchema().getXml();
      if (xmlConf != null) {
        return getRenamedNode(xmlConf, content, xmlConf.getName());
      }
    } else if (OAI3SchemaKeywords.TYPE_OBJECT.equals(schema.getSupposedType(context))) {
      return getRenamedNode(xmlConf, content, defaultKey);
    }

    return content;
  }

  private IJsonValue getRenamedNode(final Xml xmlConf, final IJsonObject content, final String defaultKey) {
    if (xmlConf != null && xmlConf.getName() != null) {
      return content.get(xmlConf.getName());
    } else if (defaultKey != null) {
      return content.get(defaultKey);
    }

    return content;
  }
}
