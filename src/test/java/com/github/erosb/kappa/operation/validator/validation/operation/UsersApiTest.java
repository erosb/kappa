package com.github.erosb.kappa.operation.validator.validation.operation;

import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.ValidationException;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import com.github.erosb.kappa.schema.validator.ValidationData;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultResponse;
import com.github.erosb.kappa.operation.validator.validation.RequestValidator;
import com.github.erosb.kappa.parser.OpenApi3Parser;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.github.erosb.kappa.operation.validator.model.Request.Method.POST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static com.github.erosb.kappa.operation.validator.model.Request.Method.GET;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class UsersApiTest
  extends OperationValidatorTestBase {

  @Parameterized.Parameters
  public static Collection<Object[]> params() {
    return Arrays.asList(
      new Object[]{"/users/users-api.yaml"},
      new Object[]{"/users/users-api-split.yaml"}
    );
  }

  private final String usersApiPath;

  public UsersApiTest(String usersApiPath) {
    this.usersApiPath = usersApiPath;
  }

  private static OpenApiValidationFailure failureByMessage(Collection<OpenApiValidationFailure> failures, String msg) {
    return failures.stream()
      .filter(c -> c.getMessage().equals(msg))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("could not find failure with message " + msg));
  }

  @Test
  public void invalidRequest()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users", POST)
      .header("content-type", "application/json")
      .body(Body.from("{\"email\":2}"))
      .build();

    ValidationException actual = assertThrows(ValidationException.class,
      () -> new RequestValidator(api).validate(request)
    );
    OpenApiValidationFailure failure = actual.results().get(0);
    assertEquals("$request.body#/email", failure.describeInstanceLocation());
    System.out.println(failure.describeSchemaLocation());
    assertTrue(failure.describeSchemaLocation().contains("/properties/email"));
  }

  @Test
  @Parameterized.Parameters
  public void invalidResponse()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users", GET)
      .header("content-type", "application/json")
      .build();

    DefaultResponse invalidResp = new DefaultResponse.Builder(200)
      .header("Content-Type", "application/json")
      .body(Body.from("[{\"id\":-5}, {\"userId\":4}]"))
      .build();

    ValidationException actual = assertThrows(ValidationException.class,
      () -> new RequestValidator(api).validate(invalidResp, request)
    );
    OpenApiValidationFailure negativeId = failureByMessage(actual.results(), "-5 is lower than minimum 0");
    assertEquals(negativeId.describeInstanceLocation(), "$response.body#/0/id");
    assertTrue(negativeId.describeSchemaLocation().endsWith("users/common-types.yaml#/Identifier"));
  }

  @Test
  public void testRequest()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
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

    ValidationException actual = assertThrows(ValidationException.class,
      () -> new RequestValidator(api).validate(invalidResp, request)
    );

    List<OpenApiValidationFailure> results = actual.results();
    assertEquals(2, results.size());

    System.out.println(results);

    OpenApiValidationFailure negativeId = failureByMessage(results, "-5 is lower than minimum 0");
    assertEquals(negativeId.describeInstanceLocation(), "$response.body#/0/id");
    assertTrue(negativeId.describeSchemaLocation().endsWith("users/common-types.yaml#/Identifier"));
    assertEquals(1, negativeId.getInstanceLocation().getLineNumber());
    assertEquals(8, negativeId.getInstanceLocation().getPosition());

    OpenApiValidationFailure wrongProp = failureByMessage(results, "the instance is not equal to any enum values");
    assertEquals(wrongProp.describeInstanceLocation(), "$response.body#/1/userId");
    System.out.println(wrongProp.describeSchemaLocation());
    //    assertTrue(wrongProp.describeSchemaLocation().endsWith("users/users-api.yaml#/components/schemas/User/propertyNames"));
  }

  @Test
  public void testVersionsEndpoint()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users/versions", GET).build();
    new RequestValidator(api).validate(request);
  }

  @Test
  public void malformedRequestBody()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users", POST)
      .header("content-type", "application/json")
      .body(Body.from("{           {ooh:this:[is,bad]")).build();
    ValidationException actual = assertThrows(ValidationException.class, () ->
      new RequestValidator(api).validate(request));

    OpenApiValidationFailure failure = actual.results().get(0);
    assertEquals("could not parse request body: Unexpected character found: {", failure.getMessage());
    assertEquals("$request.body", failure.describeInstanceLocation());
    assertEquals(1, failure.getInstanceLocation().getLineNumber());
    assertEquals(13, failure.getInstanceLocation().getPosition());
    assertTrue(failure.describeSchemaLocation().endsWith("requestBody"));
  }

  @Test
  public void unknownResponseCode()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    DefaultRequest request = new DefaultRequest.Builder("/users", GET).build();
    DefaultResponse invalidResp = new DefaultResponse.Builder(280)
      .header("Content-Type", "application/json")
      .body(Body.from("[{\"id\":-5}, {\"userId\":4}]"))
      .build();

    ValidationException exc = assertThrows(ValidationException.class, () ->
      new RequestValidator(api).validate(invalidResp, request)
    );

    OpenApiValidationFailure.StatusCodeValidationFailure failure =
      (OpenApiValidationFailure.StatusCodeValidationFailure) exc.results().get(0);

    assertEquals("Unknown status code 280", failure.getMessage());
    assertEquals("$response.status", failure.describeInstanceLocation());
  }

  @Test
  public void formatValidation()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    assertNotNull(api.getPath("/users").getPost()
      .getRequestBody()
      .getContentMediaType("application/json")
      .getSchema()
    );

    DefaultRequest request = new DefaultRequest.Builder("/users", POST)
      .header("content-type", "application/json")
      .body(Body.from("{\"email\":\"xx123\"}")).build();
    ValidationException actual = assertThrows(ValidationException.class, () ->
      new RequestValidator(api).validate(request));

    OpenApiValidationFailure failure = failureByMessage(actual.results(), "instance does not match format 'email'");

    System.out.println(failure.describeSchemaLocation());
    assertEquals("$request.body#/email", failure.describeInstanceLocation());
  }

  @Test
  public void requestSchemaIsParsed()
    throws Exception {
    URL specPath = getClass().getResource(usersApiPath);
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    assertNotNull(api.getPath("/users").getPost()
      .getRequestBody()
      .getContentMediaType("application/json")
      .getSchema()
    );

    new RequestValidator(api);
  }
}
