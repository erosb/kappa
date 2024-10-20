package org.openapi4j.schema.validator;

import com.github.erosb.jsonsKema.ValidationFailure;
import org.openapi4j.core.validation.OpenApiValidationFailure;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the validation results for output.
 * As input, this includes user data {@code V} for delegation or variability in custom validators.
 *
 * @param <V> The type of user data object.
 *            Can be a simple boolean of complex object with custom methods.
 */
public final class ValidationData<V> {

  private final List<OpenApiValidationFailure> validationFailures = new ArrayList<>();

  public ValidationData() {

  }

  /**
   * Get the current validation results.
   *
   * @return The current results.
   */
  public List<OpenApiValidationFailure> results() {
    return validationFailures;
  }

  /**
   * Add a result.
   *
   * @param result  validation result to append.
   */
  public void add(ValidationFailure result) {
   add(OpenApiValidationFailure.bodySchemaValidationFailure(result));
  }

  public void add(OpenApiValidationFailure failure) {
    validationFailures.add(failure);
  }


  /**
   * Check if the results are below the {@code ValidationSeverity.ERROR}
   *
   * @return {@code true} if the results are below {@code ValidationSeverity.ERROR}.
   */
  public boolean isValid() {
    return validationFailures.isEmpty();
  }
}
