package com.github.erosb.kappa.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.util.IOUtil;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.operation.validator.util.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Convert supported media types to abstract tree nodes.
 */
public final class ContentConverter {
  private ContentConverter() {
  }

  public static JsonNode convert(OAIContext context,
                                 MediaType mediaType,
                                 String rawContentType,
                                 InputStream is,
                                 String str) throws IOException {

    String contentType = ContentType.getTypeOnly(rawContentType);

    if (ContentType.isJson(contentType)) {
      return is != null ? jsonToNode(is) : jsonToNode(str);
    } else if (ContentType.isXml(contentType)) {
      return is != null
        ? xmlToNode(context, mediaType.getSchema(), is)
        : xmlToNode(context, mediaType.getSchema(), str);
    } else if (ContentType.isFormUrlEncoded(contentType)) {
      return is != null
        ? formUrlEncodedToNode(context, mediaType, rawContentType, is)
        : formUrlEncodedToNode(context, mediaType, rawContentType, str);
    } else if (ContentType.isMultipartFormData(contentType)) {
      return is != null
        ? multipartToNode(context, mediaType, rawContentType, is)
        : multipartToNode(context, mediaType, rawContentType, str);
    } else { // UNKNOWN
      return is != null ? textToNode(is) : textToNode(str);
    }
  }

  private static JsonNode formUrlEncodedToNode(OAIContext context, MediaType mediaType, String rawContentType, InputStream content) throws IOException {
    String encoding = ContentType.getCharSet(rawContentType);
    return FormUrlConverter.instance().convert(context, mediaType, content, encoding);
  }

  private static JsonNode formUrlEncodedToNode(OAIContext context, MediaType mediaType, String rawContentType, String content) {
    String encoding = ContentType.getCharSet(rawContentType);
    return FormUrlConverter.instance().convert(context, mediaType, content, encoding);
  }

  private static JsonNode multipartToNode(OAIContext context, MediaType mediaType, String rawContentType, InputStream content) throws IOException {
    String encoding = ContentType.getCharSet(rawContentType);
    return MultipartConverter.instance().convert(context, mediaType, content, rawContentType, encoding);
  }

  private static JsonNode multipartToNode(OAIContext context, MediaType mediaType, String rawContentType, String content) throws IOException {
    String encoding = ContentType.getCharSet(rawContentType);
    return MultipartConverter.instance().convert(context, mediaType, content, rawContentType, encoding);
  }

  private static JsonNode jsonToNode(InputStream content) throws IOException {
    return TreeUtil.json.readTree(content);
  }

  private static JsonNode jsonToNode(String content) throws IOException {
    return TreeUtil.json.readTree(content);
  }

  private static JsonNode xmlToNode(OAIContext context, Schema schema, InputStream content) throws IOException {
    return XmlConverter.instance().convert(context, schema, IOUtil.toString(content, StandardCharsets.UTF_8.name()));
  }

  private static JsonNode xmlToNode(OAIContext context, Schema schema, String content) {
    return XmlConverter.instance().convert(context, schema, content);
  }

  private static JsonNode textToNode(InputStream content) throws IOException {
    return JsonNodeFactory.instance.textNode(IOUtil.toString(content, StandardCharsets.UTF_8.name()));
  }

  private static JsonNode textToNode(String content) {
    return JsonNodeFactory.instance.textNode(content);
  }
}
