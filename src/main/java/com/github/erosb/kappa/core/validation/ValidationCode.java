package com.github.erosb.kappa.core.validation;

/**
 * Utility functional interface to wrap validation code.
 * Internal use only to keep code clean.
 */
@FunctionalInterface
public interface ValidationCode {
  void validate() throws ValidationException;
}
