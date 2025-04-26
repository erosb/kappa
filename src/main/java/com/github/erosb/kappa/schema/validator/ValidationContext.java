package com.github.erosb.kappa.schema.validator;

import com.github.erosb.kappa.core.model.OAI;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.validation.URIFactory;

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
  private final String templatePath;
  private final String method;

  public ValidationContext(OAIContext context, String templatePath, String method) {
    this.context = context;
    this.templatePath = templatePath;
    this.method = method;
  }

  public OAIContext getContext() {
    return context;
  }

  public OperationContextUriFactory uriFactory() {
    return new OperationContextUriFactory(context, templatePath, method);
  }
}
