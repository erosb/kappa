package com.github.erosb.kappa.core.model.v3;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.model.AuthOption;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OAI3ContextTest {
  @Test
  public void simple() throws ResolutionException {
    URL specPath = getClass().getResource("/parsing/discriminator.yaml");

    OAI3Context apiContext = new OAI3Context(specPath);
    assertEquals(specPath, apiContext.getBaseUrl());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Cat"));
  }

  @Test
  public void simpleAuth() throws ResolutionException {
    URL specPath = getClass().getResource("/parsing/discriminator.yaml");

    OAI3Context apiContext = new OAI3Context(
      specPath,
      Arrays.asList(
        new AuthOption(AuthOption.Type.HEADER, "myHeader", "myValue"),
        new AuthOption(AuthOption.Type.QUERY, "myQueryParam", "myValue", url -> false)
      ));

    assertEquals(specPath, apiContext.getBaseUrl());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/schemas/Cat"));
  }

  @Test
  public void remote() throws ResolutionException, MalformedURLException {
    URL specPath = new URL("https://raw.githubusercontent.com/openapitools/openapi-generator/master/modules/openapi-generator/src/test/resources/3_1/petstore.yaml");

    OAI3Context apiContext = new OAI3Context(specPath);
    assertEquals(specPath, apiContext.getBaseUrl());
    assertNotNull(apiContext.getReferenceRegistry().getRef("#/components/requestBodies/UserArray"));
  }
}
