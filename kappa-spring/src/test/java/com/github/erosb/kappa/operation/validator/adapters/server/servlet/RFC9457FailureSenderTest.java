package com.github.erosb.kappa.operation.validator.adapters.server.servlet;

import com.github.erosb.jsonsKema.JsonPointer;
import com.github.erosb.jsonsKema.SourceLocation;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RFC9457FailureSenderTest {

  @Test
  public void test()
    throws Exception {
    var subject = ValidationFailureSender.rfc9457Sender();

    MockHttpServletResponse response = new MockHttpServletResponse();
    subject.send(new ValidationException("Houston, we have a problem", List.of(OpenApiValidationFailure.unknownStatusCode(418,
      new SourceLocation(10, 12, new JsonPointer(), new URI("file:///home/erosb/openapi/pets-api.yaml"))))), response);

    var actual = response.getContentAsString();

    JSONAssert.assertEquals("""
      {
         "type" : "https://erosb.github.io/kappa/request-validation-failure",
         "status" : 400,
         "title" : "Validation failure",
         "detail" : "Houston, we have a problem",
         "errors" : [ {
           "dataLocation" : "$response.status",
           "schemaLocation" : "openapi/pets-api.yaml",
           "message" : "Unknown status code 418"
         } ]
       }
      """, actual, true);

    assertEquals("application/problem+json", response.getContentType());
  }
}
