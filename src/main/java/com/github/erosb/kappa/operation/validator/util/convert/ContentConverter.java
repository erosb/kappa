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

  public static JsonNode convert(final OAIContext context,
                                 final MediaType mediaType,
                                 final String rawContentType,
                                 final InputStream is,
                                 final String str) throws IOException {

    String contentType = ContentType.getTypeOnly(rawContentType);

    if (ContentType.isJson(contentType)) {
      return is != null ? jsonToNode(is) : jsonToNode(str);
    } else if (ContentType.isXml(contentType)) {
      System.out.println("yes it is xml " + is);
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

  private static JsonNode formUrlEncodedToNode(final OAIContext context, final MediaType mediaType, final String rawContentType, final InputStream content) throws IOException {
    String encoding = ContentType.getCharSet(rawContentType);
    return FormUrlConverter.instance().convert(context, mediaType, content, encoding);
  }

  private static JsonNode formUrlEncodedToNode(final OAIContext context, final MediaType mediaType, final String rawContentType, final String content) {
    String encoding = ContentType.getCharSet(rawContentType);
    return FormUrlConverter.instance().convert(context, mediaType, content, encoding);
  }

  private static JsonNode multipartToNode(final OAIContext context, final MediaType mediaType, final String rawContentType, InputStream content) throws IOException {
    String encoding = ContentType.getCharSet(rawContentType);
    return MultipartConverter.instance().convert(context, mediaType, content, rawContentType, encoding);
  }

  private static JsonNode multipartToNode(final OAIContext context, final MediaType mediaType, final String rawContentType, final String content) throws IOException {
    String encoding = ContentType.getCharSet(rawContentType);
    return MultipartConverter.instance().convert(context, mediaType, content, rawContentType, encoding);
  }

  private static JsonNode jsonToNode(InputStream content) throws IOException {
    return TreeUtil.json.readTree(content);
  }

  private static JsonNode jsonToNode(String content) throws IOException {
    return TreeUtil.json.readTree(content);
  }

  private static JsonNode xmlToNode(final OAIContext context, final Schema schema, InputStream content) throws IOException {
    return XmlConverter.instance().convert(context, schema, IOUtil.toString(content, StandardCharsets.UTF_8.name()));
  }

  private static JsonNode xmlToNode(final OAIContext context, final Schema schema, String content) {
    return XmlConverter.instance().convert(context, schema, content);
  }

  private static JsonNode textToNode(InputStream content) throws IOException {
    return JsonNodeFactory.instance.textNode(IOUtil.toString(content, StandardCharsets.UTF_8.name()));
  }

  private static JsonNode textToNode(String content) {
    return JsonNodeFactory.instance.textNode(content);
  }
}
