package com.github.erosb.kappa.parser;

import com.github.erosb.kappa.core.exception.ResolutionException;
import com.github.erosb.kappa.core.model.AuthOption;
import com.github.erosb.kappa.core.model.OAI;
import com.github.erosb.kappa.core.validation.ValidationException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Base Open API parser
 *
 * @param <O> Open API model
 */
public abstract class OpenApiParser<O extends OAI> {
  private static final String INVALID_FILE = "File must be specified";
  private static final String INVALID_URL = "Unable to read from url";

  /**
   * Parse the Open API specification from the given file.
   *
   * @param specFile The given file.
   * @param validate {@code true} for validation, {@code false} otherwise.
   * @return The Open API model
   * @throws ResolutionException In case of wrong path, JSON reference issue.
   * @throws ValidationException In case of validation error.
   */
  public O parse(File specFile, boolean validate) throws ResolutionException, ValidationException {
    if (specFile == null) {
      throw new ResolutionException(INVALID_FILE);
    }

    try {
      return parse(specFile.toURI().toURL(), validate);
    } catch (MalformedURLException e) {
      throw new ResolutionException(INVALID_URL, e);
    }
  }

  /**
   * Parse the Open API specification from the given URL.
   *
   * @param url      The given URL.
   * @param validate {@code true} for validation, {@code false} otherwise.
   * @return The Open API model
   * @throws ResolutionException In case of wrong path, JSON reference issue.
   * @throws ValidationException In case of validation error.
   */
  public O parse(URL url, boolean validate) throws ResolutionException, ValidationException {
    return parse(url, null, validate);
  }

  /**
   * Parse the Open API specification from the given URL with authentication values.
   *
   * @param url         The given URL.
   * @param authOptions The given authentication values for all the chain to resolve.
   * @param validate    {@code true} for validation, {@code false} otherwise.
   * @return The Open API model
   * @throws ResolutionException In case of wrong path, JSON reference issue.
   * @throws ValidationException In case of validation error.
   */
  public abstract O parse(URL url, List<AuthOption> authOptions, boolean validate) throws ResolutionException, ValidationException;
}
