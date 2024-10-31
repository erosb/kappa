package com.github.erosb.kappa.operation.validator.model.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.operation.validator.util.ContentType;
import com.github.erosb.kappa.operation.validator.util.convert.ContentConverter;
import com.github.erosb.kappa.parser.model.v3.MediaType;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;

import static java.util.Objects.requireNonNull;

public class Body {
  private static final String BODY_REQUIRED_ERR_MSG = "Body content is required.";

  private final JsonNode bodyNode;
  private final String bodyStr;
  private final Reader bodyIs;

  private Body(String body) {
    this.bodyNode = null;
    this.bodyStr = body;
    this.bodyIs = null;
  }

  private Body(Reader bodyIs) {
    this.bodyNode = null;
    this.bodyStr = null;
    this.bodyIs = bodyIs;
  }

  /**
   * Constructs a body from the given abstract node model.
   * This is the preferred way to build a body wrapper.
   *
   * @param body the given abstract node model (JSON, XML, form data, ...)
   * @return The constructed body.
   */
  @Deprecated
  public static Body from(JsonNode body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
      try {
          return new Body(TreeUtil.json.writeValueAsString(body));
      } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
      }
  }

  /**
   * Constructs a body from the given string.
   *
   * @param body The given body string.
   * @return The constructed body.
   */
  public static Body from(String body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  /**
   *
   * @param body The given body stream. The current implementation will use the system default character set to decode it into
   *             string while parsing as json.
   * @return The constructed body.
   * @see {@link Charset#defaultCharset()}
   */
  public static Body from(Reader body) {
    requireNonNull(body, BODY_REQUIRED_ERR_MSG);
    return new Body(body);
  }

  public IJsonValue contentAsNode(final String rawContentType, final URI documentSource) {
    String contentType = ContentType.getTypeOnly(rawContentType);
    if (!ContentType.isJson(contentType)) {
      throw new UnsupportedOperationException("content type " + rawContentType + " is not supported");
    }
    if (bodyIs != null) {
      return new JsonParser(bodyIs, documentSource).parse();
    } else if (bodyStr != null) {
      return new JsonParser(bodyStr, documentSource).parse();
    }
    throw new IllegalStateException("both bodyIs and bodyStr are null");
  }

  @Deprecated
  public JsonNode getContentAsNode(final OAIContext context,
                                   final MediaType mediaType,
                                   final String rawContentType) throws IOException {
    if (bodyNode != null) {
      return bodyNode;
    } else {
      return ContentConverter.convert(context, mediaType, rawContentType, bodyIs, bodyStr);
    }
  }
}
