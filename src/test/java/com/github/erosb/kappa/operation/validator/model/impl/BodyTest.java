package com.github.erosb.kappa.operation.validator.model.impl;

import com.fasterxml.jackson.databind.JsonNode;

import com.github.erosb.kappa.core.util.TreeUtil;
import org.junit.Test;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BodyTest {

  @Test
  public void fromString() throws Exception {
    String values = "{\"key\":\"value\"}";
    Body body = Body.from(values);

    checkBody(body, TreeUtil.json.readTree(values));
  }

  @Test
  public void fromJsonNode() throws Exception {
    String values = "{\"key\":\"value\"}";
    Body body = Body.from(TreeUtil.json.readTree(values));

    checkBody(body, TreeUtil.json.readTree(values));
  }

  @Test
  public void fromInputStream() throws Exception {
    String values = "{\"key\":\"value\"}";
    Body body = Body.from(new ByteArrayInputStream(values.getBytes()));

    checkBody(body, TreeUtil.json.readTree(new ByteArrayInputStream(values.getBytes())));
  }

  private void checkBody(Body body, JsonNode values) throws IOException {
    Schema schema = new Schema();
    schema.setProperty("key", new Schema().setType("string"));

    JSONAssert.assertEquals(
      values.toString(),
      body.getContentAsNode(null, new MediaType().setSchema(schema), "application/json").toString(), false);
  }
}
