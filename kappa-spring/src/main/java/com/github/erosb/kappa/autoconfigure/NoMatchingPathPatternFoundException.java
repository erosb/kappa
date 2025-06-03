package com.github.erosb.kappa.autoconfigure;

import org.springframework.web.util.pattern.PathPattern;

import java.util.Set;
import java.util.stream.Collectors;

public class NoMatchingPathPatternFoundException
  extends RuntimeException {

  private final String requestPath;
  private final Set<PathPattern> definedPathPatterns;

  public NoMatchingPathPatternFoundException(String requestPath, Set<PathPattern> definedPathPatterns) {
    this.requestPath = requestPath;
    this.definedPathPatterns = definedPathPatterns;
  }

  @Override
  public String getMessage() {
    return "OpenAPI description not found for request path " + requestPath + System.lineSeparator()
      + "Configured path patterns in KappaSpringConfiguration: " + System.lineSeparator()
      + definedPathPatterns.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator()));
  }
}
