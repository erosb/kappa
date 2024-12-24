package com.github.erosb.kappa.operation.validator.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.model.v3.OAI3;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.core.validation.URIFactory;
import com.github.erosb.kappa.parser.model.v3.AbsParameter;
import com.github.erosb.kappa.parser.model.v3.MediaType;
import com.github.erosb.kappa.parser.model.v3.Schema;
import com.github.erosb.kappa.schema.validator.JsonValidator;
import com.github.erosb.kappa.schema.validator.SKemaBackedJsonValidator;
import com.github.erosb.kappa.schema.validator.ValidationContext;
import com.github.erosb.kappa.schema.validator.ValidationData;
import com.github.erosb.kappa.parser.model.OpenApiSchema;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class ParameterValidator<M extends OpenApiSchema<M>> {

  private final ValidationContext<OAI3> context;
  private final Map<String, JsonValidator> specValidators;
  private final Map<String, AbsParameter<M>> specParameters;
  private final URIFactory uriFactory = new URIFactory();

  ParameterValidator(ValidationContext<OAI3> context, Map<String, AbsParameter<M>> specParameters) {
    this.context = context;
    this.specParameters = specParameters;
    specValidators = initValidators(specParameters);
  }

  Map<String, AbsParameter<M>> getParameters() {
    return specParameters;
  }

  void validate(final Map<String, JsonNode> values,
                final ValidationData<?> validation) {

    if (specValidators == null) {
      return;
    }

    for (Map.Entry<String, JsonValidator> entry : specValidators.entrySet()) {
      String paramName = entry.getKey();

      if (checkRequired(paramName, specParameters.get(paramName), values, validation)) {
        JsonNode paramValue = values.get(paramName);
        entry.getValue().validate(paramValue, uriFactory.pathParam(paramName), validation);
      }
    }
  }

  private Map<String, JsonValidator> initValidators(Map<String, AbsParameter<M>> specParameters) {
    if (specParameters == null || specParameters.isEmpty()) {
      return null;
    }

    Map<String, JsonValidator> validators = new HashMap<>();

    for (Map.Entry<String, AbsParameter<M>> paramEntry : specParameters.entrySet()) {
      String paramName = paramEntry.getKey();
      AbsParameter<M> parameter = paramEntry.getValue();
      Schema paramSchema = null;

      if (parameter.getContentMediaTypes() != null) {
        for (Map.Entry<String, MediaType> entry : parameter.getContentMediaTypes().entrySet()) {
          MediaType mediaType = entry.getValue();
          paramSchema = mediaType.getSchema();
          break;
        }
      } else {
        paramSchema = parameter.getSchema();
      }

      if (paramSchema != null) {
        JsonValidator v = new SKemaBackedJsonValidator(paramSchema.copy(), context);
        validators.put(paramName, v);
      }
    }

    return validators.isEmpty() ? null : validators;
  }

  private boolean checkRequired(final String paramName,
                                final AbsParameter<?> parameter,
                                final Map<String, JsonNode> paramValues,
                                final ValidationData<?> validation) {

    if (!paramValues.containsKey(paramName)) {
      if (parameter.isRequired()) {
        validation.add(OpenApiValidationFailure.missingRequiredParameter(paramName));
      }
      return false;
    }

    return true;
  }
}
