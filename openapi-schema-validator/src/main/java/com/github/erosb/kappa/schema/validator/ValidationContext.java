package com.github.erosb.kappa.schema.validator;

import com.github.erosb.kappa.core.model.OAI;
import com.github.erosb.kappa.core.model.OAIContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Validation context and option bag.
 *
 * @param <O> The Open API version type.
 */
@SuppressWarnings("UnusedReturnValue")
public class ValidationContext<O extends OAI> {
  private final OAIContext context;
  private final Map<String, JsonValidator> visitedRefs = new HashMap<>();
  private final Map<Byte, Boolean> defaultOptions = new HashMap<>();
  private boolean isFastFail;

  public ValidationContext(OAIContext context) {
    this.context = context;
  }

  public OAIContext getContext() {
    return context;
  }

  /**
   * Get the fast fail behaviour status.
   *
   * @return The fast fail behaviour status.
   */
  public boolean isFastFail() {
    return isFastFail;
  }

  /**
   * Set the fast fail behaviour.
   *
   * @param fastFail {@code true} for fast failing.
   */
  public ValidationContext<O> setFastFail(boolean fastFail) {
    isFastFail = fastFail;
    return this;
  }

  /**
   * Add a reference to avoid looping.
   * This is internally used, you should not call this directly.
   *
   * @param ref       The reference expression.
   * @param validator The associated validator.
   */
  public ValidationContext<O> addReference(String ref, JsonValidator validator) {
    visitedRefs.put(ref, validator);
    return this;
  }

  /**
   * Get a visited reference validator in any.
   * This is internally used, you should not call this directly.
   *
   * @param ref The reference expression.
   * @return The associated validator.
   */
  public JsonValidator getReference(String ref) {
    return visitedRefs.get(ref);
  }

  public ValidationContext<O> setOption(byte option, boolean value) {
    defaultOptions.put(option, value);
    return this;
  }

  /**
   * Get the value from the given option name.
   *
   * @param option The given option.
   * @return The corresponding value, {@code false} if the option is not set.
   */
  public boolean getOption(byte option) {
    return Boolean.TRUE.equals(defaultOptions.get(option));
  }

}
