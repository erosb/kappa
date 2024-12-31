package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.github.erosb.kappa.parser.model.v3.OpenApi3;

import java.util.function.Function;

@FunctionalInterface
public interface OpenApiLookup extends Function<String, OpenApi3> {

  OpenApi3 apply(String requestPath);

}
