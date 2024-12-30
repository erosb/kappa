package com.github.erosb.kappa.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.model.AuthOption;
import com.github.erosb.kappa.core.model.v3.OAI3Context;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;

import java.net.URL;
import java.util.List;

/**
 * The parser for Open API v3.x.x
 */
public class OpenApi3Parser extends OpenApiParser<OpenApi3> {
  private static final String NULL_SPEC_URL = "Failed to load spec from 'null' location";
  private static final String INVALID_SPEC = "Failed to load spec at '%s'";

  /**
   * {@inheritDoc}
   */
  @Override
  public OpenApi3 parse(URL url, List<AuthOption> authOptions, boolean validate) throws ResolutionException, ValidationException {
    if (url == null) {
      throw new ResolutionException(NULL_SPEC_URL);
    }

    OpenApi3 api;

    try {
      OAI3Context context = new OAI3Context(url, authOptions);
      JsonNode baseDocument = context.getBaseDocument();
      api = TreeUtil.json.convertValue(baseDocument, OpenApi3.class);
      api.setContext(context);
    } catch (IllegalArgumentException e) {
      throw new ResolutionException(String.format(INVALID_SPEC, url.toString()), e);
    }

    return api;
  }
}
