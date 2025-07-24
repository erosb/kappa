package com.github.erosb.kappa.autoconfigure;

import com.github.erosb.kappa.operation.validator.adapters.server.servlet.ValidationFailureSender;

import java.util.LinkedHashMap;
import java.util.List;

public class KappaSpringConfiguration {
  private LinkedHashMap<String, String> openapiDescriptions = new LinkedHashMap<>();

  private ValidationFailureSender validationFailureSender = ValidationFailureSender.defaultSender();
  private List<String> ignoredPathPatterns;

  public LinkedHashMap<String, String> getOpenapiDescriptions() {
    return openapiDescriptions;
  }

  public void setOpenapiDescriptions(LinkedHashMap<String, String> openapiDescriptions) {
    this.openapiDescriptions = openapiDescriptions;
  }

  public ValidationFailureSender getValidationFailureSender() {
    return validationFailureSender;
  }

  public void setValidationFailureSender(
    ValidationFailureSender validationFailureSender) {
    this.validationFailureSender = validationFailureSender;
  }

  public void setIgnoredPathPatterns(String... whitelistPathPatterns) {
    this.ignoredPathPatterns = List.of(whitelistPathPatterns);
  }

  public List<String> getIgnoredPathPatterns() {
    return ignoredPathPatterns;
  }
}
