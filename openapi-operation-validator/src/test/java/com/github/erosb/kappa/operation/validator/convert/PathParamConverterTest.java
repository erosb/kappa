package com.github.erosb.kappa.operation.validator.convert;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.OpenApi3Util;
import com.github.erosb.kappa.operation.validator.util.PathResolver;
import com.github.erosb.kappa.operation.validator.util.convert.ParameterConverter;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.Parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PathParamConverterTest {

  @Test
  public void pathSimpleNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleNotExplodedPrimitive", "5");
    ParamChecker.checkPrimitive(nodes, "simpleNotExplodedPrimitive");
  }

  @Test
  public void pathSimpleExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleExplodedPrimitive", "5");
    ParamChecker.checkPrimitive(nodes, "simpleExplodedPrimitive");
  }

  @Test
  public void pathSimpleNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleNotExplodedArray", "3,4,5");
    ParamChecker.checkArray(nodes, "simpleNotExplodedArray");
  }

  @Test
  public void pathSimpleExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleExplodedArray", "3,4,5");
    ParamChecker.checkArray(nodes, "simpleExplodedArray");
  }

  @Test
  public void pathSimpleNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleNotExplodedObject", "boolProp,true,stringProp,admin");
    ParamChecker.checkObject(nodes, "simpleNotExplodedObject");
  }

  @Test
  public void pathSimpleExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("simpleExplodedObject", "boolProp=true,stringProp=admin");
    ParamChecker.checkObject(nodes, "simpleExplodedObject");
  }

  // --------------- LABEL -------------------
  // -----------------------------------------
  @Test
  public void pathLabelNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelNotExplodedPrimitive", ".5");
    ParamChecker.checkPrimitive(nodes, "labelNotExplodedPrimitive");
  }

  @Test
  public void pathLabelExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelExplodedPrimitive", ".5");
    ParamChecker.checkPrimitive(nodes, "labelExplodedPrimitive");
  }

  @Test
  public void pathLabelNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelNotExplodedArray", ".3,4,5");
    ParamChecker.checkArray(nodes, "labelNotExplodedArray");
  }

  @Test
  public void pathLabelExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelExplodedArray", ".3.4.5");
    ParamChecker.checkArray(nodes, "labelExplodedArray");
  }

  @Test
  public void pathLabelNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelNotExplodedObject", ".boolProp,true,stringProp,admin");
    ParamChecker.checkObject(nodes, "labelNotExplodedObject");
  }

  @Test
  public void pathLabelExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("labelExplodedObject", ".boolProp=true.stringProp=admin");
    ParamChecker.checkObject(nodes, "labelExplodedObject");
  }

  // --------------- MATRIX -------------------
  // -----------------------------------------
  @Test
  public void pathMatrixNotExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixNotExplodedPrimitive", ";matrixNotExplodedPrimitive=5");
    ParamChecker.checkPrimitive(nodes, "matrixNotExplodedPrimitive");
  }

  @Test
  public void pathMatrixExplodedPrimitive() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixExplodedPrimitive", ";matrixExplodedPrimitive=5");
    ParamChecker.checkPrimitive(nodes, "matrixExplodedPrimitive");
  }

  @Test
  public void pathMatrixNotExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixNotExplodedArray", ";matrixNotExplodedArray=3,4,5");
    ParamChecker.checkArray(nodes, "matrixNotExplodedArray");
  }

  @Test
  public void pathMatrixExplodedArray() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixExplodedArray", ";matrixExplodedArray=3;matrixExplodedArray=4;matrixExplodedArray=5");
    ParamChecker.checkArray(nodes, "matrixExplodedArray");
  }

  @Test
  public void pathMatrixNotExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixNotExplodedObject", ";matrixNotExplodedObject=boolProp,true,stringProp,admin");
    ParamChecker.checkObject(nodes, "matrixNotExplodedObject");
  }

  @Test
  public void pathMatrixExplodedObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("matrixExplodedObject", ";boolProp=true;stringProp=admin");
    ParamChecker.checkObject(nodes, "matrixExplodedObject");
  }

  @Test
  public void pathContentObject() throws Exception {
    Map<String, JsonNode> nodes = pathToNode("content", "{\"boolProp\":true,\"stringProp\":\"admin\"}");
    ParamChecker.checkObject(nodes, "content");
  }

  // --------------- Misc. -------------------
  // -----------------------------------------
  @Test
  public void noPathPattern() {
    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();

    Map<String, JsonNode> values = ParameterConverter.pathToNode(null, parameters, null, "/foo");
    assertNotNull(values);
    assertTrue(values.isEmpty());
  }

  private Map<String, JsonNode> pathToNode(String parameterName, String value) throws Exception {
    OpenApi3 api = OpenApi3Util.loadApi("/operation/parameter/pathParameters.yaml");

    Map<String, AbsParameter<Parameter>> parameters = new HashMap<>();
    parameters.put(parameterName, api.getComponents().getParameters().get(parameterName));

    Pattern pattern = PathResolver.instance().solve("/" + parameterName + "/{" + parameterName + "}");

    return ParameterConverter.pathToNode(
      api.getContext(),
      parameters,
      pattern,
      "/" + parameterName + "/" + value);
  }
}
