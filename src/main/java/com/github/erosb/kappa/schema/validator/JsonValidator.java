package com.github.erosb.kappa.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

/**
 * Representation of a validator.
 */
public interface JsonValidator {
  /**
   * Validate the given value from the validation setup.
   *
   * @param valueNode The given value to check.
   * @param validation   The result stack to append any additional info from the validation.
   * @return {@code true} if chain should continue for the current keyword, {@code false} otherwise.
   */
  boolean validate(JsonNode valueNode, URI documentSource, ValidationData<?> validation);
}
