package com.github.erosb.kappa.schema.validator;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class SKemaBackedJsonValidatorTest {

  @Test
  public void rewriteNestedJarUrl() throws Exception {
    URI actual = SKemaBackedJsonValidator.rewriteProbableJarUrl(new URI("jar:nested:/examples/kappa-spring-boot-examples.jar/!BOOT-INF/classes/!/openapi/pets-api.yaml"));
    assertEquals(new URI("classpath://openapi/pets-api.yaml"), actual);
  }

}
