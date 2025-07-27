package com.github.erosb.kappa.autoconfigure;

import org.junit.Test;

import static org.junit.Assert.*;

public class PathPatternMatchingOpenApiLookupTest {

  @Test
  public void successWithDefaultConfig() {
    new PathPatternMatchingOpenApiLookup(new KappaSpringConfiguration());
  }

}
