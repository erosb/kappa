package com.github.erosb.kappa.operation.validator.validation.operation;

import com.github.erosb.kappa.operation.validator.model.Request;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultResponse;
import com.github.erosb.kappa.operation.validator.validation.OperationValidator;
import com.github.erosb.kappa.parser.OpenApi3Parser;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import com.github.erosb.kappa.parser.model.v3.Operation;
import com.github.erosb.kappa.parser.model.v3.Path;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;

public class PathTest extends OperationValidatorTestBase {
  @Test
  @Ignore
  public void checkReferences() throws Exception {
    URL specPath = OperationValidatorTestBase.class.getResource("/operation/operationValidator.yaml");
    OpenApi3 api = new OpenApi3Parser().parse(specPath, false);

    Path path = api.getPath("/refPath").getReference(api.getContext()).getMappedContent(Path.class);
    Operation op = path.getOperation("post");

    OperationValidator val = new OperationValidator(api, path, op);

    Body body = Body.from("{\"objectType\": \"string\",\"value\": \"foo\"}");

    check(
      new DefaultResponse.Builder(200).header("Content-Type", "application/json").body(body).build(),
      val::validateBody,
      true);
  }

  @Test
  public void pathCheck() throws Exception {
    OperationValidator val = loadOperationValidator("/operation/operationValidator.yaml", "paramCheck");

    // String can be also a number
    check(
      new DefaultRequest.Builder("/fixed/1/fixed/2/fixed/", Request.Method.GET).build(),
      val::validatePath,
      true);


    // 'string' is not a number
    check(
      new DefaultRequest.Builder("https://api.com/fixed/string/fixed/2/fixed/", Request.Method.GET).build(),
      val::validatePath,
      false);

    // wrong path
    check(
      new DefaultRequest.Builder("https://api.com/fixed/fixed/2/fixed/", Request.Method.GET).build(),
      val::validatePath,
      false);

    // Empty string is not valid
    check(
      new DefaultRequest.Builder("https://api.com/fixed/1/fixed//fixed/", Request.Method.GET).build(),
      val::validatePath,
      false);


    // Validation with full fixed path template
    val = loadOperationValidator("/operation/operationValidator.yaml", "merge_parameters");

    check(
      new DefaultRequest.Builder("/merge_parameters", Request.Method.GET).build(),
      val::validatePath,
      true);

    check(
      new DefaultRequest.Builder("/foo/bar/merge_parameters", Request.Method.GET).build(),
      val::validatePath,
      false);

    check(
      new DefaultRequest.Builder("https://api.com/foo/bar/merge_parameters", Request.Method.GET).build(),
      val::validatePath,
      false);
  }
}
