package com.github.erosb.kappa.operation.validator.util.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.IOUtil;
import com.github.erosb.kappa.core.util.MultiStringMap;
import com.github.erosb.kappa.core.util.StringUtil;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.operation.validator.util.convert.style.FormStyleConverter;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.EncodingProperty;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Parameter;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.operation.validator.util.convert.style.DeepObjectStyleConverter;
import com.github.erosb.kappa.operation.validator.util.convert.style.PipeDelimitedStyleConverter;
import com.github.erosb.kappa.operation.validator.util.convert.style.SpaceDelimitedStyleConverter;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

class FormUrlConverter {
  private static final String SPACE_DELIMITED = "spaceDelimited";
  private static final String PIPE_DELIMITED = "pipeDelimited";
  private static final String DEEP_OBJECT = "deepObject";

  private static final FormUrlConverter INSTANCE = new FormUrlConverter();

  public static FormUrlConverter instance() {
    return INSTANCE;
  }

  private FormUrlConverter() {
  }

  private final Map<MediaType, Map<String, AbsParameter<Parameter>>> mediaTypesCache = new HashMap<>();

  JsonNode convert(final OAIContext context, final MediaType mediaType, final InputStream body, String encoding) throws IOException {
    return convert(context, mediaType, IOUtil.toString(body, encoding), encoding);
  }

  JsonNode convert(final OAIContext context, final MediaType mediaType, final String body, final String encoding) {
    Map<String, JsonNode> params = convert(context, getParameters(mediaType), body, true, encoding);
    return TreeUtil.json.valueToTree(params);
  }

  Map<String, JsonNode> convert(final OAIContext context,
                                final Map<String, AbsParameter<Parameter>> specParameters,
                                final String body,
                                final boolean caseSensitive,
                                final String encoding) {

    final Map<String, JsonNode> mappedValues = new HashMap<>();

    if (body == null) {
      return mappedValues;
    }

    MultiStringMap<String> paramPairs = getParameterPairs(body, caseSensitive, encoding);
    List<String> visitedParams = new ArrayList<>();

    for (Map.Entry<String, AbsParameter<Parameter>> paramEntry : specParameters.entrySet()) {
      final String specParamName = paramEntry.getKey();
      final AbsParameter<Parameter> specParam = paramEntry.getValue();
      final JsonNode convertedValue;

      if (specParam.getSchema() != null) {
        final String style = specParam.getStyle();

        if (SPACE_DELIMITED.equals(style)) {
          convertedValue = SpaceDelimitedStyleConverter.instance().convert(context, specParam, specParamName, paramPairs, visitedParams);
        } else if (PIPE_DELIMITED.equals(style)) {
          convertedValue = PipeDelimitedStyleConverter.instance().convert(context, specParam, specParamName, paramPairs, visitedParams);
        } else if (DEEP_OBJECT.equals(style)) {
          convertedValue = DeepObjectStyleConverter.instance().convert(context, specParam, specParamName, paramPairs, visitedParams);
        } else { // form is the default
          if (specParam.getExplode() == null) { // explode true is default
            specParam.setExplode(true);
          }
          convertedValue = FormStyleConverter.instance().convert(context, specParam, specParamName, paramPairs, visitedParams);
        }
      } else {
        convertedValue = getValueFromContentType(context, specParam.getContentMediaTypes(), specParamName, paramPairs, visitedParams);
      }

      if (convertedValue != null) {
        mappedValues.put(specParamName, convertedValue);
      }
    }

    // Remove visited parameters
    for (String key : visitedParams) {
      paramPairs.remove(key);
    }

    // add remaining & unknown properties as string to the result
    Schema defaultSchema = new Schema().setType(OAI3SchemaKeywords.TYPE_STRING);
    for (Map.Entry<String, Collection<String>> valueEntry : paramPairs.entrySet()) {
      Collection<String> values = valueEntry.getValue();

      JsonNode value = TypeConverter.instance().convertPrimitive(
        context,
        defaultSchema,
        values.iterator().next());

      mappedValues.put(valueEntry.getKey(), value);
    }

    return mappedValues;
  }

  private MultiStringMap<String> getParameterPairs(String value, boolean caseSensitive, String encoding) {
    List<String> pairs = StringUtil.tokenize(value, "&", true, true);
    MultiStringMap<String> result = new MultiStringMap<>(caseSensitive);

    for (String pair : pairs) {
      int idx = pair.indexOf('=');
      if (idx == -1) {
        result.put(decode(pair, encoding), null);
      } else {
        result.put(
          decode(pair.substring(0, idx), encoding),
          decode(pair.substring(idx + 1), encoding));
      }
    }

    return result;
  }

  private String decode(String value, String encoding) {
    try {
      return URLDecoder.decode(value, encoding);
    } catch (UnsupportedEncodingException e) {
      try {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException ignored) {
        return value; // Will never happen - value is coming from JDK
      }
    }
  }

  private JsonNode getValueFromContentType(final OAIContext context,
                                           final Map<String, MediaType> mediaTypes,
                                           final String paramName,
                                           final MultiStringMap<String> paramPairs,
                                           final List<String> visitedParams) {

    Collection<String> propValues = paramPairs.get(paramName);
    if (propValues == null) {
      return null;
    }

    visitedParams.add(paramName);

    if (mediaTypes != null) {
      Optional<Map.Entry<String, MediaType>> entry = mediaTypes.entrySet().stream().findFirst();

      if (entry.isPresent()) {
        Map.Entry<String, MediaType> mediaType = entry.get();

        try {
          return ContentConverter.convert(
            context,
            mediaType.getValue(),
            mediaType.getKey(),
            null,
            propValues.stream().findFirst().orElse(null));
        } catch (IOException e) {
          return null;
        }
      }
    }

    return null;
  }

  /**
   * Transform media type to Map of Parameters to work with same structure for:
   * - query
   * - form-data
   * - x-www-formurlencoded.
   *
   * @param mediaType The given meida type to transform.
   * @return The Map of parameters.
   */
  private Map<String, AbsParameter<Parameter>> getParameters(final MediaType mediaType) {
    // check cache
    Map<String, AbsParameter<Parameter>> specParameters = mediaTypesCache.get(mediaType);
    if (specParameters != null) {
      return specParameters;
    }

    // Cache missed
    Map<String, EncodingProperty> encodings
      = mediaType.getEncodings() != null
      ? mediaType.getEncodings()
      : new HashMap<>();

    specParameters = new HashMap<>();

    for (Map.Entry<String, Schema> propEntry : mediaType.getSchema().getProperties().entrySet()) {
      String propName = propEntry.getKey();

      specParameters.put(
        propName,
        createParameter(encodings, propName, propEntry.getValue()));
    }

    // Add media type to cache
    mediaTypesCache.put(mediaType, specParameters);

    return specParameters;
  }

  private AbsParameter<Parameter> createParameter(final Map<String, EncodingProperty> encodings,
                                                  final String propName,
                                                  final Schema schema) {

    EncodingProperty encodingProperty = encodings.get(propName);

    Parameter param = new Parameter().setName(propName);
    param.setSchema(schema);

    if (encodingProperty != null) {
      param
        .setStyle(encodingProperty.getStyle())
        .setExplode(encodingProperty.getExplode());

      if (encodingProperty.getContentType() != null) {
        param.setContentMediaType(encodingProperty.getContentType(), new MediaType().setSchema(schema));
        param.setSchema(null); // reset schema
      }
    }

    return param;
  }
}
