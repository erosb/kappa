package com.github.erosb.kappa.operation.validator.convert;

import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonNull;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.OpenApi3Util;
import com.github.erosb.kappa.operation.validator.util.convert.ParameterConverter;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Parameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HeaderParamConverterTest {
  @Test
  public void headerSimpleNotExplodedPrimitive() throws Exception {
    check(
      "simpleNotExplodedPrimitive",
      Collections.singleton("5"),
      Collections.singleton("wrong"),
      ParamChecker::checkPrimitive,
      ParamChecker::checkWrongPrimitive);
  }

  @Test
  public void headerSimpleExplodedPrimitive() throws Exception {
    check(
      "simpleExplodedPrimitive",
      Collections.singleton("5"),
      Collections.singleton("wrong"),
      ParamChecker::checkPrimitive,
      ParamChecker::checkWrongPrimitive);
  }

  @Test
  public void headerSimpleNotExplodedArray() throws Exception {
    check(
      "simpleNotExplodedArray",
      Arrays.asList("3", "4", "5"),
      Collections.singleton("wrong"),
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  @Test
  public void headerSimpleExplodedArray() throws Exception {
    check(
      "simpleExplodedArray",
      Arrays.asList("3", "4", "5"),
      Collections.singleton("wrong"),
      ParamChecker::checkArray,
      ParamChecker::checkWrongArray);
  }

  @Test
  public void headerSimpleNotExplodedObject() throws Exception {
    check(
      "simpleNotExplodedObject",
      Arrays.asList("boolProp", "true", "stringProp", "admin"),
      Arrays.asList("boolProp", "wrong"),
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  @Test
  public void headerSimpleExplodedObject() throws Exception {
    check(
      "simpleExplodedObject",
      Arrays.asList("boolProp=true", "stringProp=admin"),
      Collections.singleton("boolProp=wrong"),
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  @Test
  public void headerContentObject() throws Exception {
    check(
      "content",
      Collections.singletonList("{\"boolProp\":true,\"stringProp\":\"admin\"}"),
      Collections.singleton("{\"boolProp\":\"wrong\"}"),
      ParamChecker::checkObject,
      ParamChecker::checkWrongObject);
  }

  protected void check(String parameterName,
                       Collection<String> validValue,
                       Collection<String> invalidValue,
                       BiConsumer<Map<String, IJsonValue>, String> validChecker,
                       BiConsumer<Map<String, IJsonValue>, String> invalidChecker) throws Exception {

    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/headerParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    // Valid check
    Map<String, Collection<String>> values = new HashMap<>();
    values.put(parameterName, validValue);
    validChecker.accept(mapToNodes(api.getContext(), parameters, values), parameterName);
    // Invalid check
    values.put(parameterName, invalidValue);
    invalidChecker.accept(mapToNodes(api.getContext(), parameters, values), parameterName);

    // null value
    values.put(parameterName, null);
    assertTrue(mapToNodes(api.getContext(), parameters, values).get(parameterName) instanceof JsonNull);

    // unlinked param/value
    // empty map
    values.clear();
    assertNull(mapToNodes(api.getContext(), parameters, values).get(parameterName));
    // null map
    assertNull(mapToNodes(api.getContext(), parameters, null).get(parameterName));
  }

  private Map<String, IJsonValue> mapToNodes(OAIContext context,
                                           Map<String, AbsParameter<Parameter>> parameters,
                                           Map<String, Collection<String>> values) {
    return ParameterConverter.headersToNode(context, parameters, values);
  }
}
