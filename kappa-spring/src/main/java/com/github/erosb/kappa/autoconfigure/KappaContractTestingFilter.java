package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.JakartaServletRequest;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.MemoizingServletRequest;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiLookup;
import com.github.erosb.kappa.operation.validator.model.Request;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;

public class KappaContractTestingFilter
  implements Filter {

  static KappaContractTestingFilter forApiLookup(PathPatternMatchingOpenApiLookup lookupFn) {
    return new KappaContractTestingFilter(lookupFn);
  }

  private final OpenApiLookup lookupFn;

  private KappaContractTestingFilter(OpenApiLookup lookupFn) {
    this.lookupFn = lookupFn;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
    throws IOException, ServletException {
    try {
      HttpServletRequest memoizedReq = new MemoizingServletRequest((HttpServletRequest) req);

      Request jakartaRequest = JakartaServletRequest.of(memoizedReq);

      new RequestValidator(lookupFn.apply(jakartaRequest.getPath())).validate(jakartaRequest);

      filterChain.doFilter(req, resp);
    } catch (ValidationException e) {
      throw new AssertionError(e.results().stream().map(failure -> describeFailure(failure)).collect(Collectors.joining("\n")));
    }
  }

  @NotNull
  private static String describeFailure(OpenApiValidationFailure failure) {
    return failure.getMessage() + System.lineSeparator()
      + "instance location: " + failure.describeInstanceLocation() + System.lineSeparator()
      + "schema location: " + failure.describeSchemaLocation() + System.lineSeparator()
      ;
  }
}
