package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Function;

@FunctionalInterface
public interface OpenApiLookup
  extends Function<String, OpenApi3> {

  OpenApi3 apply(String requestPath);

  default void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response,
                               FilterChain filterChain)
    throws Exception {

  }

}
