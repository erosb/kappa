package com.github.erosb.kappa.operation.validator.convert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.github.erosb.kappa.operation.validator.OpenApi3Util;
import com.github.erosb.kappa.operation.validator.util.ContentType;
import com.github.erosb.kappa.operation.validator.util.convert.ContentConverter;
import com.github.erosb.kappa.parser.model.v3.EncodingProperty;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.*;

public class ContentConverterTest {
  private static OpenApi3 api;

  @BeforeClass
  public static void setup() throws Exception {
    api = OpenApi3Util.loadApi("/operation/contentType/contentType.yaml");
  }

  @Test
  public void testFormUrlEncoded() throws Exception {
    check("/operation/contentType/formurl.json");
  }

  @Test
  public void testMultipart() throws Exception {
    check("/operation/contentType/multipart.json");
  }

  @Test
  public void testJson() throws Exception {
    check("/operation/contentType/json.json");
  }

  @Test @Ignore
  public void testXml() throws Exception {
    check("/operation/contentType/xml.json");
  }

  @Test
  public void testDirect() {
    assertEquals(StandardCharsets.UTF_8.name(), ContentType.getCharSet(null));
    assertFalse(ContentType.isMultipartFormData("foo"));
    assertTrue(ContentType.isMultipartFormData("multipart/mixed"));
    assertNull(ContentType.getCharSetOrNull(null));
    assertNull(ContentType.getCharSetOrNull("application/x-www-form-urlencoded; charset=foo"));
  }

  private void check(String testPath) throws Exception {
    ArrayNode testCases = (ArrayNode) TreeUtil.json.readTree(ContentConverterTest.class.getResource(testPath));

    for (int index = 0; index < testCases.size(); index++) {
      JsonNode testCase = testCases.get(index);
      JsonNode schemaModelName = testCase.get("schemaModel");
      JsonNode contentType = testCase.get("contentType");
      JsonNode encodings = testCase.get("encodings");
      JsonNode inputData = testCase.get("input");
      JsonNode expectedData = testCase.get("expected");

      MediaType mediaType = new MediaType()
        .setSchema(api.getComponents().getSchemas().get(schemaModelName.textValue()))
        .setEncodings(TreeUtil.json.convertValue(encodings, new TypeReference<Map<String, EncodingProperty>>() {}));
      System.out.println("check " + index);
      check(
        mediaType,
        contentType.textValue(),
        inputData.textValue(),
        expectedData.toString(),
        testCase.get("description").textValue());
    }
  }

  private void check(MediaType mediaType, String contentType, String input, String expected, String description) throws Exception {
    // With string
    JsonNode actual = ContentConverter.convert(api.getContext(), mediaType, contentType, null, input);
    System.out.println(actual.toString());
    JSONAssert.assertEquals(
      String.format("JSON matching test failed on test '%s'", description),
      expected,
      actual.toString(),
      true);

    // With input stream
    actual = ContentConverter.convert(
      api.getContext(),
      mediaType,
      contentType,
      new ByteArrayInputStream(input != null ? input.getBytes() : "".getBytes()), null);

    JSONAssert.assertEquals(
      String.format("JSON matching test failed on test '%s'", description),
      expected,
      actual.toString(),
      true);
  }
}
