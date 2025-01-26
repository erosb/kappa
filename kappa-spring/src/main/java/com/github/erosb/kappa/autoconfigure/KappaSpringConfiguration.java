package com.github.erosb.kappa.autoconfigure;

import java.util.LinkedHashMap;

public class KappaSpringConfiguration {
  private LinkedHashMap<String, String> openapiDescriptions;

  public LinkedHashMap<String, String> getOpenapiDescriptions() {
    return openapiDescriptions;
  }

  public void setOpenapiDescriptions(LinkedHashMap<String, String> openapiDescriptions) {
    this.openapiDescriptions = openapiDescriptions;
  }
}
