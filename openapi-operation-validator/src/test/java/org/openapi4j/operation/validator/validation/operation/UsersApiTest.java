package org.openapi4j.operation.validator.validation.operation;

import org.junit.Test;
import org.openapi4j.operation.validator.model.impl.DefaultRequest;
import org.openapi4j.operation.validator.validation.OperationValidator;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.model.v3.Operation;

import java.net.URL;

import static org.openapi4j.operation.validator.model.Request.Method.GET;

public class UsersApiTest extends OperationValidatorTestBase {

  @Test
  public void test() throws Exception {
    URL specPath = getClass().getResource("/users/users-api.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    Operation operation = api.getPath("/users").getOperation("get");

//    new OperationValidator(api, path, operation).validateBody();

    DefaultRequest request = new DefaultRequest.Builder("/users", GET).build();
    new RequestValidator(api).validate(request);
  }
}
