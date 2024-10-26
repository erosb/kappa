package com.github.erosb.kappa.operation.validator.validation.operation;

import com.github.erosb.kappa.operation.validator.validation.OperationValidator;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;

import static com.github.erosb.kappa.operation.validator.model.Request.Method.GET;

public class QueryTest extends OperationValidatorTestBase {
  @Test
  public void queryCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true").build(),
      val::validateQuery,
      true);

    // currently we dont support nullable keyword here
//    check(
//      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true&stringQueryParam").build(),
//      val::validateQuery,
//      true);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=false&intQueryParam=12").build(),
      val::validateQuery,
      true);

    // nullable
    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=false&intQueryParam=").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=yes").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam").build(),
      val::validateQuery,
      false);

    check(
      new DefaultRequest.Builder("/foo", GET).query("boolQueryParam=true&intQueryParam").build(),
      val::validateQuery,
      false);

    // required
    check(
      new DefaultRequest.Builder("/foo", GET).build(),
      val::validateQuery,
      false);
  }
}
