package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiLookup;
import com.github.erosb.kappa.parser.OpenApi3Parser;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;

public class SpringOpenApiLookup implements OpenApiLookup {
  @Override
  public OpenApi3 apply(String requestPath) {
    try {
      return new OpenApi3Parser().parse(getClass().getResource("/openapi/users-api.yaml"), false);
    } catch (ResolutionException | ValidationException e) {
      throw new RuntimeException(e);
    }
  }
}
