package com.github.erosb.kappa.operation.validator.convert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.erosb.jsonsKema.IJsonValue;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

abstract class ParamChecker {
  static void checkPrimitive(Map<String, IJsonValue> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(5, nodes.get(propName).requireInt());
  }

  static void checkWrongPrimitive(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(JsonNodeFactory.instance.textNode("wrong"), nodes.get(propName));
  }

  static void checkArray(Map<String, IJsonValue> nodes, String propName) {
    System.out.println("nodes = " + nodes);
    System.out.println("propName = " + propName);
    assertEquals(1, nodes.size());
    assertEquals(3, nodes.get(propName).requireArray().length());
    assertEquals(3, nodes.get(propName).requireArray().get(0).requireInt());
    assertEquals(4, nodes.get(propName).requireArray().get(1).requireInt());
    assertEquals(5, nodes.get(propName).requireArray().get(2).requireInt());
  }

  static void checkWrongArray(Map<String, IJsonValue> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(1, nodes.get(propName).l);
    assertEquals(JsonNodeFactory.instance.arrayNode().add(JsonNodeFactory.instance.textNode("wrong")), nodes.get(propName));
  }

  static void checkObject(Map<String, IJsonValue> nodes, String propName) {
    System.out.println("nodes = " + nodes);
    System.out.println("propName = " + propName);
    assertEquals(1, nodes.size());
    assertEquals("admin", nodes.get(propName).requireObject().get("stringProp").requireString().getValue());
    assertTrue(nodes.get(propName).requireObject().get("boolProp").requireBoolean().getValue());
  }

  static void checkWrongObject(Map<String, JsonNode> nodes, String propName) {
    assertEquals(1, nodes.size());
    assertEquals(JsonNodeFactory.instance.objectNode().put("boolProp", "wrong"), nodes.get(propName));
  }
}
