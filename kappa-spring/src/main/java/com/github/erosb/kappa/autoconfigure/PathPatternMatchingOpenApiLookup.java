package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiLookup;
import com.github.erosb.kappa.parser.OpenApi3Parser;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.LinkedHashMap;
import java.util.Map;

public class PathPatternMatchingOpenApiLookup
  implements OpenApiLookup {

  private final Map<PathPattern, OpenApi3> pathPatternToApiDescr;

  public PathPatternMatchingOpenApiLookup(KappaSpringConfiguration configuration) {
    pathPatternToApiDescr = new LinkedHashMap<>();
    configuration.getOpenapiDescriptions().forEach((rawPathPattern, apiDescriptionPath) -> {
        try {
          if (!rawPathPattern.startsWith("/")) {
            rawPathPattern = "/" + rawPathPattern;
          }
          pathPatternToApiDescr.put(new PathPatternParser().parse(rawPathPattern),
            new OpenApi3Parser().parse(getClass().getResource(apiDescriptionPath), false));
        } catch (ResolutionException | ValidationException e) {
          throw new RuntimeException(e);
        }
      }
    );
  }

  @Override
  public OpenApi3 apply(String requestPath) {
    PathContainer path = PathContainer.parsePath(requestPath);
    for (Map.Entry<PathPattern, OpenApi3> entry : pathPatternToApiDescr.entrySet()) {
      if (entry.getKey().matches(path)) {
        return entry.getValue();
      }
    }
    throw new RuntimeException("OpenAPI description not found for request path " + requestPath);
  }
}
