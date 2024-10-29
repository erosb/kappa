package com.github.erosb.kappa.operation.validator.validation.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.operation.validator.validation.OperationValidator;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;

import static com.github.erosb.kappa.operation.validator.model.Request.Method.POST;

public class DiscriminatorTest extends OperationValidatorTestBase {
  @Test
  public void discriminatorCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "discriminator");

    String body = "{\"pet_type\": \"Cat\", \"age\": 3}";
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = "{\"pet_type\": \"Dog\", \"bark\": true}";
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = "{\"pet_type\": \"Dog\", \"bark\": false, \"breed\": \"Dingo\"}";
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      true);

    body = "{\"age\": 3}";
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      false);

    body = "{\"pet_type\": \"Dog\", \"bark\": false, \"breed\": \"foo\"}";
    check(
      new DefaultRequest.Builder("/discriminator", POST).header("Content-Type", "application/json").body(Body.from(body)).build(),
      val::validateBody,
      false);
  }
}
