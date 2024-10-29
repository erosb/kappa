package com.github.erosb.kappa.core.validation;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Representation of a validation error.
 */
public class ValidationException extends Exception {
  private final List<OpenApiValidationFailure> validationFailures;

  public ValidationException(String message) {
    this(message, emptyList());
  }

  public ValidationException(String message, List<OpenApiValidationFailure> failures) {
    super(message);
    this.validationFailures = failures;
  }

  /**
   * Get associated results from the validation.
   * @return The validation results.
   */
  public List<OpenApiValidationFailure> results() {
    return validationFailures;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    if (getMessage() != null) {
      builder.append(getMessage());
    }
    validationFailures.forEach(failure -> builder.append(
      String.format("%n%s", failure.getMessage())
    ));

    return builder.toString();
  }
}
