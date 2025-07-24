package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.operation.validator.model.Request;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class OpenApiBasedRequestValidationFilter
  implements Filter {

  public static OpenApiBasedRequestValidationFilter forApiDescription(
    OpenApi3 api,
    ValidationFailureSender validationFailureSender
  ) {
    return new OpenApiBasedRequestValidationFilter(path -> api, validationFailureSender);
  }

  public static OpenApiBasedRequestValidationFilter forApiLookup(
    OpenApiLookup lookupFn,
    ValidationFailureSender validationFailureSender
  ) {
    return new OpenApiBasedRequestValidationFilter(lookupFn, validationFailureSender);
  }

  private final OpenApiLookup lookupFn;

  private final ValidationFailureSender validationFailureSender;

  private OpenApiBasedRequestValidationFilter(OpenApiLookup lookupFn, ValidationFailureSender validationFailureSender) {
    this.lookupFn = requireNonNull(lookupFn);
    this.validationFailureSender = requireNonNull(validationFailureSender);
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws IOException {
    HttpServletResponse httpResp = (HttpServletResponse) resp;
    try {

      // we need to wrap the original request instance into a MemoizingServletRequest,
      // since we will need to parse the request body twice: once for the OpenAPI-validation
      // and once for the jackson parsing.
      // basic HttpServletRequests cannot be read twice, hence we use the
      // MemoizingServletRequest shipped with Kappa
      // more here: https://www.baeldung.com/spring-reading-httpservletrequest-multiple-times
      HttpServletRequest memoizedReq = new MemoizingServletRequest((HttpServletRequest) req);

      // Kappa can understand different representations of HTTP requests and responses
      // here we use the Servlet API specific adapter of Kappa, to get a Kappa Request instance
      // which wraps a HttpServletRequest
      Request jakartaRequest = JakartaServletRequest.of(memoizedReq);

      OpenApi3 api = null;
      try {
        api = lookupFn.apply(jakartaRequest.getPath());
      } catch (Exception e) {
        lookupFn.handleException(e, memoizedReq, httpResp, chain);
      }

      if (api != null) {
        // we do the validation
        new RequestValidator(api).validate(jakartaRequest);

        // if no request validation error was found, we proceed with the request execution
        chain.doFilter(memoizedReq, httpResp);
      }

    } catch (ValidationException ex) {
      // if the request validation failed, we represents the validation failures in a simple
      // json response and send it back to the client
      validationFailureSender.send(ex, httpResp);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
