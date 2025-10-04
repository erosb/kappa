package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiLookup;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import com.github.erosb.kappa.parser.OpenApi3Parser;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PathPatternMatchingOpenApiLookup
  implements OpenApiLookup {

  private final Map<PathPattern, OpenApi3> pathPatternToApiDescr;

  private final List<PathPattern> ignoredPathPatterns;

  public PathPatternMatchingOpenApiLookup(KappaSpringConfiguration configuration) {
    pathPatternToApiDescr = new LinkedHashMap<>();
    Map<String, OpenApi3> pathToParsedDescription = new HashMap<>();
    configuration.getOpenapiDescriptions().forEach((rawPathPattern, apiDescriptionPath) -> {
        try {
          if (!rawPathPattern.startsWith("/")) {
            rawPathPattern = "/" + rawPathPattern;
          }

          OpenApi3 openApi3 = pathToParsedDescription.get(apiDescriptionPath);
          if (openApi3 == null) {
            openApi3 = new OpenApi3Parser().parse(getClass().getResource(apiDescriptionPath), false);
            pathToParsedDescription.put(apiDescriptionPath, openApi3);
          }
          pathPatternToApiDescr.put(new PathPatternParser().parse(rawPathPattern), openApi3);
        } catch (ResolutionException | ValidationException e) {
          throw new RuntimeException(e);
        }
      }
    );
    ignoredPathPatterns = configuration.getIgnoredPathPatterns().stream()
      .map(p -> new PathPatternParser().parse(p))
      .toList();
  }

  @Override
  public OpenApi3 apply(String requestPath) {
    PathContainer path = PathContainer.parsePath(requestPath);
    for (Map.Entry<PathPattern, OpenApi3> entry : pathPatternToApiDescr.entrySet()) {
      if (entry.getKey().matches(path)) {
        return entry.getValue();
      }
    }
    throw new NoMatchingPathPatternFoundException(requestPath, pathPatternToApiDescr.keySet());
  }

  @Override
  public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response,
                              FilterChain filterChain)
    throws Exception {
    URI requestURI = URI.create(request.getRequestURL().toString());
    var requestPath = requestURI.getPath();
    PathContainer path = PathContainer.parsePath(requestPath);
    if (exception instanceof NoMatchingPathPatternFoundException) {
      if (ignoredPathPatterns.stream().anyMatch(pattern -> pattern.matches(path))) {
        filterChain.doFilter(request, response);
      } else {
        throw new ValidationException(RequestValidator.INVALID_OP_PATH_ERR_MSG.formatted(requestURI.toString()));
      }
    } else {
      throw new RuntimeException(exception);
    }
  }
}
