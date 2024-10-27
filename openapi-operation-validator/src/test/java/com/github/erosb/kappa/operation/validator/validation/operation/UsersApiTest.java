package com.github.erosb.kappa.operation.validator.validation.operation;

import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultResponse;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import com.github.erosb.kappa.parser.OpenApi3Parser;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import static com.github.erosb.kappa.operation.validator.model.Request.Method.POST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static com.github.erosb.kappa.operation.validator.model.Request.Method.GET;
import static org.junit.Assert.fail;

public class UsersApiTest extends OperationValidatorTestBase {

  private static OpenApiValidationFailure failureByMessage(Collection<OpenApiValidationFailure> failures, String msg) {
    return failures.stream()
      .filter(c -> c.getMessage().equals(msg))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("could not find failure with message " + msg));
  }

  @Test
  public void testRequest() throws Exception {
    URL specPath = getClass().getResource("/users/users-api.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users", GET).build();
    new RequestValidator(api).validate(request);

    DefaultResponse resp = new DefaultResponse.Builder(200)
      .header("Content-Type", "application/json")
      .body(Body.from("[{\"id\":2}]"))
      .build();

    new RequestValidator(api).validate(resp, request);

    DefaultResponse invalidResp = new DefaultResponse.Builder(200)
      .header("Content-Type", "application/json")
      .body(Body.from("[{\"id\":-5}, {\"userId\":4}]"))
      .build();

    try {
      new RequestValidator(api).validate(invalidResp, request);
      fail("should throw ValidationException");
    } catch (ValidationException e) {
      List<OpenApiValidationFailure> results = e.results();
      assertEquals(2, results.size());

      OpenApiValidationFailure negativeId = failureByMessage(results, "-5 is lower than minimum 0");
      assertEquals(negativeId.describeInstanceLocation(), "$request.body#/0/id");
      assertTrue(negativeId.describeSchemaLocation().endsWith("users/common-types.yaml#/Identifier"));

      OpenApiValidationFailure wrongProp = failureByMessage(results, "actual instance is not the same as expected constant value");
      assertEquals(wrongProp.describeInstanceLocation(), "$request.body#/1/userId");
      assertTrue(wrongProp.describeSchemaLocation().endsWith("users/users-api.yaml#/components/schemas/User/propertyNames/const"));
    }
  }

  @Test
  public void testVersionsEndpoint() throws Exception {
    URL specPath = getClass().getResource("/users/users-api.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users/versions", GET).build();
    new RequestValidator(api).validate(request);
  }

  @Test
  public void malformedRequestBody() throws Exception {
    URL specPath = getClass().getResource("/users/users-api.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users", POST)
      .header("content-type", "application/json")
      .body(Body.from("{{ooh:this:[is,bad]")).build();
    ValidationException actual = assertThrows(ValidationException.class, () ->
      new RequestValidator(api).validate(request));

    actual.results().forEach(failure -> {
      assertEquals("$request.body", failure.describeInstanceLocation());
      assertTrue(failure.describeSchemaLocation().endsWith("requestBody"));
      System.out.println("failure.describeSchemaLocation() = " + failure.describeSchemaLocation());
      System.out.println("failure.getMessage() = " + failure.getMessage());
    });
  }
}
