package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.JakartaServletRequest;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.MemoizingServletRequest;
import com.github.erosb.kappa.operation.validator.adapters.server.servlet.OpenApiLookup;
import com.github.erosb.kappa.operation.validator.model.Request;
import com.github.erosb.kappa.operation.validator.model.Response;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class MockMvcServletResponse
  implements Response {

  public MockMvcServletResponse(MockHttpServletResponse original) {
    this.original = original;
  }

  private final MockHttpServletResponse original;

  @Override
  public int getStatus() {
    return original.getStatus();
  }

  @Override
  public Body getBody() {
    try {
      return Body.from(original.getContentAsString());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<String, Collection<String>> getHeaders() {
    Map<String, Collection<String>> rval = new HashMap<>();
    original.getHeaderNames().forEach(name -> {
      rval.put(name, original.getHeaders(name));
    });
    return rval;
  }

  @Override
  public Collection<String> getHeaderValues(String name) {
    return getHeaders().getOrDefault(name, Collections.emptyList());
  }
}

class KappaPrintingResultHandler
  extends PrintingResultHandler {

  protected KappaPrintingResultHandler(final PrintWriter writer) {
    super(new PrintingResultHandler.ResultValuePrinter() {
      public void printHeading(String heading) {
        writer.println();
        writer.println(String.format("%s:", heading));
      }

      public void printValue(String label, @Nullable Object value) {
        if (value != null && value.getClass().isArray()) {
          value = CollectionUtils.arrayToList(value);
        }

        writer.println(String.format("%17s = %s", label, value));
      }
    });
  }

  void handle(MockHttpServletRequest request, MockHttpServletResponse response) {
    try {
      ResultValuePrinter printer = getPrinter();
      printer.printHeading("MockHttpServletRequest");
      printRequest(request);
      if (response != null) {
        printer.printHeading("MockHttpServletResponse");
        printResponse(response);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

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
    MockHttpServletResponse mockResponse = null;
    try {
      HttpServletRequest memoizedReq = new MemoizingServletRequest((HttpServletRequest) req);

      Request jakartaRequest = JakartaServletRequest.of(memoizedReq);

      RequestValidator validator = new RequestValidator(lookupFn.apply(jakartaRequest.getPath()));
      validator.validate(jakartaRequest);

      filterChain.doFilter(memoizedReq, resp);
      mockResponse = (MockHttpServletResponse) resp;
      MockMvcServletResponse mockResp = new MockMvcServletResponse(mockResponse);
      validator.validate(mockResp, jakartaRequest);
    } catch (ValidationException e) {
      PrintWriter pw = new PrintWriter(System.out);
      KappaPrintingResultHandler printingResultHandler = new KappaPrintingResultHandler(pw);
      printingResultHandler.handle((MockHttpServletRequest) req, mockResponse);
      pw.println();
      pw.flush();
      throw new AssertionError(e.getMessage() + " \n" + e.results().stream().map(KappaContractTestingFilter::describeFailure)
        .collect(Collectors.joining("\n")));
    } catch (NoMatchingPathPatternFoundException e) {
      throw new AssertionError(e.getMessage());
    }
  }

  @NotNull
  private static String describeFailure(OpenApiValidationFailure failure) {
    return failure.getMessage() + System.lineSeparator()
      + "instance location: " + failure.describeInstanceLocation() + System.lineSeparator()
      + "schema location: " + failure.describeSchemaLocation() + System.lineSeparator()
      + appendDynamicPathInfo(failure)
      ;
  }

  private static String appendDynamicPathInfo(OpenApiValidationFailure failure) {
    if (failure instanceof OpenApiValidationFailure.SchemaValidationFailure) {
      return "\tevaluated on dynamic path: "
        + ((OpenApiValidationFailure.SchemaValidationFailure) failure).getFailure().getDynamicPath() + System.lineSeparator();
    }
    return "";
  }
}
