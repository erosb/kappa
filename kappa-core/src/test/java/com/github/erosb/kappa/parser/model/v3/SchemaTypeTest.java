package com.github.erosb.kappa.parser.model.v3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.util.TreeUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SchemaTypeTest {

  @Test
  public void testSingleTypeString() {
    Schema schema = new Schema();
    schema.setType("string");

    assertEquals("string", schema.getType());
    assertNotNull(schema.getTypes());
    assertEquals(1, schema.getTypes().size());
    assertEquals("string", schema.getTypes().get(0));
    assertFalse(schema.hasMultipleTypes());
  }

  @Test
  public void testMultipleTypes() {
    Schema schema = new Schema();
    List<String> types = Arrays.asList("string", "null");
    schema.setTypes(types);

    assertEquals("string", schema.getType()); // Returns first type
    assertNotNull(schema.getTypes());
    assertEquals(2, schema.getTypes().size());
    assertEquals("string", schema.getTypes().get(0));
    assertEquals("null", schema.getTypes().get(1));
    assertTrue(schema.hasMultipleTypes());
  }

  @Test
  public void testDeserializeSingleTypeFromJson() throws Exception {
    String json = "{\"type\": \"integer\"}";
    Schema schema = TreeUtil.json.readValue(json, Schema.class);

    assertEquals("integer", schema.getType());
    assertNotNull(schema.getTypes());
    assertEquals(1, schema.getTypes().size());
    assertEquals("integer", schema.getTypes().get(0));
    assertFalse(schema.hasMultipleTypes());
  }

  @Test
  public void testDeserializeArrayTypeFromJson() throws Exception {
    String json = "{\"type\": [\"string\", \"null\"]}";
    Schema schema = TreeUtil.json.readValue(json, Schema.class);

    assertEquals("string", schema.getType()); // Returns first type
    assertNotNull(schema.getTypes());
    assertEquals(2, schema.getTypes().size());
    assertEquals("string", schema.getTypes().get(0));
    assertEquals("null", schema.getTypes().get(1));
    assertTrue(schema.hasMultipleTypes());
  }

  @Test
  public void testSerializeSingleType() throws Exception {
    Schema schema = new Schema();
    schema.setType("boolean");

    String json = TreeUtil.json.writeValueAsString(schema);
    JsonNode node = TreeUtil.json.readTree(json);

    assertTrue(node.has("type"));
    assertTrue(node.get("type").isTextual());
    assertEquals("boolean", node.get("type").asText());
  }

  @Test
  public void testSerializeArrayType() throws Exception {
    Schema schema = new Schema();
    schema.setTypes(Arrays.asList("number", "null"));

    String json = TreeUtil.json.writeValueAsString(schema);
    JsonNode node = TreeUtil.json.readTree(json);

    assertTrue(node.has("type"));
    assertTrue(node.get("type").isArray());
    assertEquals(2, node.get("type").size());
    assertEquals("number", node.get("type").get(0).asText());
    assertEquals("null", node.get("type").get(1).asText());
  }

  @Test
  public void testRoundTripSingleType() throws Exception {
    Schema original = new Schema();
    original.setType("array");

    String json = TreeUtil.json.writeValueAsString(original);
    Schema deserialized = TreeUtil.json.readValue(json, Schema.class);

    assertEquals(original.getType(), deserialized.getType());
    assertEquals(original.getTypes(), deserialized.getTypes());
  }

  @Test
  public void testRoundTripArrayType() throws Exception {
    Schema original = new Schema();
    original.setTypes(Arrays.asList("string", "integer", "null"));

    String json = TreeUtil.json.writeValueAsString(original);
    Schema deserialized = TreeUtil.json.readValue(json, Schema.class);

    assertEquals(original.getType(), deserialized.getType());
    assertEquals(original.getTypes(), deserialized.getTypes());
  }

  @Test
  public void testCopySingleType() {
    Schema original = new Schema();
    original.setType("object");

    Schema copy = original.copy();

    assertEquals(original.getType(), copy.getType());
    assertEquals(original.getTypes(), copy.getTypes());
  }

  @Test
  public void testCopyArrayType() {
    Schema original = new Schema();
    original.setTypes(Arrays.asList("boolean", "null"));

    Schema copy = original.copy();

    assertEquals(original.getType(), copy.getType());
    assertEquals(original.getTypes(), copy.getTypes());
    assertTrue(copy.hasMultipleTypes());
  }

  @Test
  public void testSetTypeOverridesTypes() {
    Schema schema = new Schema();
    schema.setTypes(Arrays.asList("string", "null"));

    assertTrue(schema.hasMultipleTypes());

    schema.setType("integer");

    assertEquals("integer", schema.getType());
    assertFalse(schema.hasMultipleTypes());
  }

  @Test
  public void testSetTypesOverridesType() {
    Schema schema = new Schema();
    schema.setType("string");

    assertFalse(schema.hasMultipleTypes());

    schema.setTypes(Arrays.asList("number", "null"));

    assertEquals("number", schema.getType());
    assertTrue(schema.hasMultipleTypes());
  }
}
