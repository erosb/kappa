package com.github.erosb.kappa.operation.validator.adapters.server.vertx.v3.impl;

import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.parser.model.v3.Path;
import com.github.erosb.kappa.operation.validator.model.impl.RequestParameters;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import com.github.erosb.kappa.parser.model.v3.Operation;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

class OperationValidationHandler
  implements Handler<RoutingContext> {
  private static final String RQ_PARAMETERS = "rqParameters";

  private final RequestValidator requestValidator;
  private final Path path;
  private final Operation operation;

  OperationValidationHandler(RequestValidator requestValidator, Path path, Operation operation) {
    this.requestValidator = requestValidator;
    this.path = path;
    this.operation = operation;
    if (!path.getOperations().containsValue(operation)) {
      throw new IllegalArgumentException("operation is not contained in the path");
    }
  }

  @Override
  public void handle(RoutingContext rc) {
    try {
      RequestParameters rqParameters = requestValidator.validate(VertxRequest.of(rc), path, operation);
      rc.data().put(RQ_PARAMETERS, rqParameters);
      rc.next();
    } catch (ValidationException e) {
      rc.fail(400, e);
    }
  }
}
