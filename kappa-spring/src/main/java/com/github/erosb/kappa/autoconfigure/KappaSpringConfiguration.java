package com.github.erosb.kappa.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;

@ConfigurationProperties(prefix = "kappa")
public class KappaSpringConfiguration {
  private LinkedHashMap<String, String> openapiDescriptions;

  public LinkedHashMap<String, String> getOpenapiDescriptions() {
    return openapiDescriptions;
  }

  public void setOpenapiDescriptions(LinkedHashMap<String, String> openapiDescriptions) {
    this.openapiDescriptions = openapiDescriptions;
  }
}
