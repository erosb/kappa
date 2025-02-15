package com.github.erosb.kappa.operation.validator.model;

import com.github.erosb.kappa.operation.validator.model.impl.Body;

import java.util.Collection;
import java.util.Map;

public interface Response {
  /**
   * @return The response status code.
   */
  int getStatus();

  /**
   * @return The response body, if there is one.
   */
  Body getBody();

  /**
   * Get the headers.
   *
   * @return The headers.
   */
  Map<String, Collection<String>> getHeaders();

  /**
   * Get the collection of header values for the header param with the given name.
   *
   * @param name The (case insensitive) name of the parameter to retrieve
   * @return The header values for that param; or empty list. Must be {@code nonnull}.
   */
  Collection<String> getHeaderValues(final String name);

  /**
   * Get the first of header value for the header param with the given name (if any exist).
   *
   * @param name The (case insensitive) name of the parameter to retrieve
   * @return The first header value for that param (if it exists)
   */
  default String getHeaderValue(final String name) {
    Collection<String> values = getHeaderValues(name);
    if (values != null) {
      return values.stream().findFirst().orElse(null);
    }
    return null;
  }

  /**
   * Get the content-type header of this response, if it has been set.
   *
   * @return The content-type header, or null if it has not been set.
   */
  default String getContentType() {
    return getHeaderValue("Content-Type");
  }
}
