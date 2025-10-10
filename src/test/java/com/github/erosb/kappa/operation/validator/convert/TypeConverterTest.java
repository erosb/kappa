package com.github.erosb.kappa.operation.validator.convert;

import com.github.erosb.jsonsKema.IJsonArray;
import com.github.erosb.jsonsKema.IJsonValue;
import com.github.erosb.jsonsKema.JsonNull;
import org.junit.Ignore;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.util.convert.TypeConverter;
import com.github.erosb.kappa.parser.model.v3.Schema;
import org.skyscreamer.jsonassert.JSONAssert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeConverterTest {
  @Test
  public void convertObjectNullNode() {
    // no schema
    assertTrue(TypeConverter.instance().convertObject(null, null, new HashMap<>()) instanceof JsonNull);

    Schema schema = new Schema();
    schema.setType("object");

    // no properties
    assertTrue(TypeConverter.instance().convertObject(null, schema, new HashMap<>()) instanceof JsonNull);
    // empty properties
    schema.setProperties(new HashMap<>());
    assertTrue(TypeConverter.instance().convertObject(null, schema, new HashMap<>()) instanceof JsonNull);
    // no content
    schema.setProperties(new HashMap<>());
    assertTrue(TypeConverter.instance().convertObject(null, schema, null) instanceof JsonNull);

    // object of object
    schema
      .setProperty("foo", new Schema().setType("object")
        .setProperty("bar", new Schema().setType("integer")));

    Map<String, Object> value = new HashMap<>();
    value.put("bar", 1);
    Map<String, Object> rootValue = new HashMap<>();
    rootValue.put("foo", value);

    JSONAssert.assertEquals(
      "{\"foo\":{\"bar\":\"1\"}}",
      TypeConverter.instance().convertObject(null, schema, rootValue).toString(), false);
  }

  @Test
  public void convertObjectOfObject() {
    Schema schema = new Schema();
    schema
      .setType("object")
      .setProperty("foo", new Schema().setType("object")
        .setProperty("bar", new Schema().setType("integer")));

    Map<String, Object> bar = new HashMap<>();
    bar.put("bar", 1);
    Map<String, Object> foo = new HashMap<>();
    foo.put("foo", bar);

    JSONAssert.assertEquals(
      "{\"foo\":{\"bar\":\"1\"}}",
      TypeConverter.instance().convertObject(null, schema, foo).toString(), false);

    // wrong value
    foo.put("foo", "bar");
    JSONAssert.assertEquals(
      "{\"foo\":null}",
      TypeConverter.instance().convertObject(null, schema, foo).toString(), false);
  }

  @Test
  public void convertArrayNullNode() {
    // no schema
    assertTrue(TypeConverter.instance().convertArray(null, null, new ArrayList<>()) instanceof JsonNull);

    Schema schema = new Schema();

    // no content
    assertTrue(TypeConverter.instance().convertArray(null, schema, null) instanceof JsonNull);

    // empty content
    schema.setType("integer");
    IJsonValue result = TypeConverter.instance().convertArray(null, schema, new ArrayList<>());
    assertEquals(0, result.requireArray().length());

    // with values
    List<Object> values = new ArrayList<>();
    values.add(1);
    values.add(10);
    IJsonArray convertedNode = TypeConverter.instance().convertArray(null, schema, values).requireArray();
    assertEquals("1", convertedNode.get(0).requireString().getValue());
    assertEquals("10", convertedNode.get(1).requireString().getValue());
  }

  @Test
  public void convertArrayPrimitive() {
    Schema schema = new Schema();
    schema.setType("integer");

    List<Object> values = new ArrayList<>();
    values.add(1);
    values.add(10);

    IJsonArray convertedNode = TypeConverter.instance().convertArray(null, schema, values).requireArray();

    assertEquals("1", convertedNode.get(0).requireString().getValue());
    assertEquals("10", convertedNode.get(1).requireString().getValue());
  }

  @Test
  public void convertArrayObject() {
    Schema schema = new Schema();
    schema
      .setType("object")
      .setProperty("foo", new Schema().setType("object")
        .setProperty("bar", new Schema().setType("integer")));

    Map<String, Object> bar = new HashMap<>();
    bar.put("bar", 1);
    Map<String, Object> foo = new HashMap<>();
    foo.put("foo", bar);

    List<Object> value = new ArrayList<>();
    value.add(foo);

    JSONAssert.assertEquals(
      "[{\"foo\":{\"bar\":\"1\"}}]",
      TypeConverter.instance().convertArray(null, schema, value).toString(), false);

    // wrong value
    foo.put("foo", "bar");
    JSONAssert.assertEquals(
      "[{\"foo\":null}]",
      TypeConverter.instance().convertArray(null, schema, value).toString(), false);
  }

  @Test
  public void convertArrayArray() {
    Schema schema = new Schema();
    schema
      .setType("array")
      .setItemsSchema(new Schema().setType("integer"));

    List<Object> valueList = new ArrayList<>();
    valueList.add(1);
    valueList.add(2);
    List<Object> rootList = new ArrayList<>();
    rootList.add(valueList);

    JSONAssert.assertEquals(
      "[[\"1\",\"2\"]]",
      TypeConverter.instance().convertArray(null, schema, rootList).toString(), false);

    // wrong value
    valueList.add("foo");
    JSONAssert.assertEquals(
      "[[\"1\",\"2\",\"foo\"]]",
      TypeConverter.instance().convertArray(null, schema, rootList).toString(), false);

    // wrong sub list
    rootList.clear();
    rootList.add("foo");
    JSONAssert.assertEquals(
      "[null]",
      TypeConverter.instance().convertArray(null, schema, rootList).toString(), false);
  }

  @Test
  public void convertPrimitiveNullNode() {
    // no schema
    assertEquals(
      "[]",
      TypeConverter.instance().convertPrimitive(null, null, new ArrayList<>()).requireString().getValue());

    // no schema
    assertEquals(
      "foo",
      TypeConverter.instance().convertPrimitive(null, null, "foo").requireString().getValue());

    Schema schema = new Schema();

    // no content
    assertTrue(TypeConverter.instance().convertPrimitive(null, schema, null) instanceof JsonNull);

    // wrong content
    schema.setType("integer");
    assertEquals(
      "wrong",
      TypeConverter.instance().convertPrimitive(null, schema, "wrong").requireString().getValue());
  }

  @Test
  @Ignore
  public void convertPrimitiveValues() {
    Schema schema = new Schema();
    // INTEGER
    schema.setType("integer");
    // no format
    assertEquals(
      "1",
      TypeConverter.instance().convertPrimitive(null, schema, 1).requireString().getValue());
    schema.setFormat("int32");
    assertEquals(
      "1",
      TypeConverter.instance().convertPrimitive(null, schema, 1).requireString());
    schema.setFormat("int64");
    assertEquals(
      1L,
      TypeConverter.instance().convertPrimitive(null, schema, 1).requireNumber().getValue().longValue());

    // DECIMAL
    // no format
    schema.setType("number");
    schema.setFormat(null);
    assertEquals(
      BigDecimal.valueOf(1),
      TypeConverter.instance().convertPrimitive(null, schema, 1).requireNumber().getValue());
    schema.setFormat("float");
    assertEquals(
      1.0f,
      TypeConverter.instance().convertPrimitive(null, schema, 1).requireNumber().getValue().floatValue(), 0.001f);
    schema.setFormat("double");
    assertEquals(
      1.0,
      TypeConverter.instance().convertPrimitive(null, schema, 1).requireNumber().getValue().doubleValue(), 0.001);

    // BOOLEAN
    schema.setType("boolean");
    schema.setFormat(null);
    assertTrue(
      TypeConverter.instance().convertPrimitive(null, schema, "TrUe").requireBoolean().getValue());
    assertEquals(
      false,
      TypeConverter.instance().convertPrimitive(null, schema, "fAlSe").requireBoolean().getValue());
    assertEquals(
      "pofkpfosdkfsd",
      TypeConverter.instance().convertPrimitive(null, schema, "pofkpfosdkfsd").requireString().getValue());
  }
}
